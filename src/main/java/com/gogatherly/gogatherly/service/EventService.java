package com.gogatherly.gogatherly.service;

import com.gogatherly.gogatherly.dto.*;
import com.gogatherly.gogatherly.exception.ErrorResponseException;
import com.gogatherly.gogatherly.model.entity.*;
import com.gogatherly.gogatherly.model.repository.CategoryRepository;
import com.gogatherly.gogatherly.model.repository.EventRepository;
import com.gogatherly.gogatherly.model.repository.TicketRepository;
import com.gogatherly.gogatherly.model.repository.UserRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.Query;
import jakarta.persistence.TypedQuery;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Validator;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.geo.Point;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Slf4j
@Service
public class EventService {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private EntityManager entityManager;


    @Autowired
    private CategoryRepository categoryRepository;

    @Value("${file.upload}")
    private String uploadPath;

    @Autowired
    private Validator validator;
    @Autowired
    private TicketRepository ticketRepository;
    @Autowired
    private EventRepository eventRepository;

    @Transactional
    public Event createEvent(EventRequest request, MultipartFile banner) {
        log.info("masuk ker service");
        Set<ConstraintViolation<EventRequest>> validate = validator.validate(request);
        if (validate.size() != 0){
            throw new ConstraintViolationException(validate);
        }

        if(!banner.getContentType().equals("image/jpeg")){
            throw new ErrorResponseException(HttpStatus.BAD_REQUEST,"error", "file not image" );
        }

        if(!request.getStartTime().isBefore(request.getEndTime())){

            Map<String, String> error = new HashMap<>(Map.of("start_time", "The start time of the event must be earlier than the end time of the event."));

            throw new ErrorResponseException(HttpStatus.BAD_REQUEST,"error", "The start time of the event must be earlier than the end time of the event." , error );

        }

        for(int i = 0 ; i < request.getTickets().size(); i++){
            LocalDateTime startTimeTicket = request.getTickets().get(i).getStartTime();
            LocalDateTime endTimeTicket = request.getTickets().get(i).getEndTime();
            if(!startTimeTicket.isBefore(endTimeTicket)){
                Map<String, String> error =  new HashMap<>(Map.of("ticket."+i, "The start time of the ticket must be earlier than the end time of the ticket."));
                throw  new ErrorResponseException(HttpStatus.BAD_REQUEST, "error", "The start time of the event must be earlier than the end time of the event.", error);
            }

            if(!(startTimeTicket.isBefore(request.getEndTime())&&endTimeTicket.isBefore(request.getEndTime()))){
                Map<String, String> error =  new HashMap<>(Map.of("ticket."+i, "The start time ticket and end ticket not greater than end time event ."));
                throw  new ErrorResponseException(HttpStatus.BAD_REQUEST, "error", "The start time ticket and end ticket not greater than end time event .", error);
            }
        }





        String newFileName;

        try{
            String extension = banner.getOriginalFilename().substring(banner.getOriginalFilename().lastIndexOf("."));

             newFileName = UUID.randomUUID().toString()+extension;

            Path path = Path.of("upload/public/banner/" + newFileName);

            Files.write(path, banner.getBytes());

            log.info("berhasil menyimpan file");
        } catch (IOException e) {
            log.error(e.getMessage());
            System.out.println(e);
            throw new ErrorResponseException(HttpStatus.INTERNAL_SERVER_ERROR, "error", e.getMessage());
        }


        List<Category> categories = categoryRepository.findAllById(request.getCategories().stream().map(CategoriesRequest::getCategoriesId).toList());

        LocationEvent locationEvent = new LocationEvent();
        locationEvent.setCity(request.getLocation().getCity());
        locationEvent.setProvince(request.getLocation().getCity());
        locationEvent.setAddres(request.getLocation().getAddres());
        locationEvent.setLatitude(request.getLocation().getLatitude());
        locationEvent.setLongitude(request.getLocation().getLongitude());

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();


        Event event = new Event();
        event.setTitle(request.getTitle());
        event.setLocation(locationEvent);
        event.setCategories(categories);
        event.setBanner(newFileName);
        event.setStartEvent(request.getStartTime());
        event.setEndEvent(request.getEndTime());
        event.setStatus(StatusEvent.SCHEDULED);
        event.setViolation(request.getViolation());
        event.setDescription(request.getDescription());
        event.setUser((User) authentication.getPrincipal());

        eventRepository.save(event);

        List<Ticket> tickets = new ArrayList<>();

        request.getTickets().forEach(reqTicket -> {
            Ticket ticket= new Ticket();
            ticket.setTitle(reqTicket.getTitle());
            ticket.setPrice(Long.valueOf(reqTicket.getPrice()));
            ticket.setDescription(reqTicket.getDescription());
            ticket.setEndTicket(reqTicket.getEndTime());
            ticket.setStartTicket(reqTicket.getStartTime());
            ticket.setQuantity(Long.valueOf(reqTicket.getQuantity()));
            ticket.setIsFree(reqTicket.getIsFree());
            ticket.setEvent(event);

            if(reqTicket.getIsFree()){
                ticket.setPrice(0L);
            }else{
                ticket.setPrice(ticket.getPrice());
            }

            tickets.add(ticket);
        });
//        Menyimpan
        ticketRepository.saveAll(tickets);
        return event;
    }

    public EventResponse detailEvent(Integer id){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User user = (User) authentication.getPrincipal();

        Event event = eventRepository.findByIdAndUser_Id(id, user.getId())
                .orElseThrow(() -> new ErrorResponseException(HttpStatus.NOT_FOUND, "error", "Event not found"));

        LocationEventResponse locationResponse =  LocationEventResponse
                .builder()
                .city(event.getLocation().getCity())
                .province(event.getLocation().getProvince())
                .addres(event.getLocation().getAddres())
                .longitude(event.getLocation().getLongitude())
                .latitude(event.getLocation().getLatitude())
                .build();

        List<TicketResponse> ticketResponses = new ArrayList<>();
        event.getTickets().forEach(ticket -> {
            TicketResponse response = new TicketResponse();
            response.setTitle(ticket.getTitle());
            response.setPrice(ticket.getPrice());
            response.setStartTime(ticket.getStartTicket());
            response.setEndTime(ticket.getEndTicket());
            response.setDescription(ticket.getDescription());
            response.setQuantity(ticket.getQuantity());
            response.setIsFree(ticket.getIsFree());
            ticketResponses.add(response);
        });

        List<String> categoriesResponse = new ArrayList<>();

        for (Category category : event.getCategories()){
            categoriesResponse.add(category.getName());
        }

        return EventResponse
                .builder()
                .status(event.getStatus().name())
                .id(event.getId())
                .title(event.getTitle())
                .banner(event.getBanner())
                .categories(categoriesResponse)
                .location(locationResponse)
                .tickets(ticketResponses)
                .description(event.getDescription())
                .violation(event.getViolation())
                .startTime(event.getStartEvent())
                .endTime(event.getEndEvent())
                .createdAt(event.getCreatedAt())
                .updatedAt(event.getUpdatedAt())
                .build();

    }

    public void updateEvent(EventRequest request, Integer id, MultipartFile banner){
        Set<ConstraintViolation<EventRequest>> validate = validator.validate(request);
        if (validate.size() != 0){
            throw new ConstraintViolationException(validate);
        }


        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Event event = eventRepository.findByIdAndUser_Id(id, user.getId())
                .orElseThrow(() -> new ErrorResponseException(HttpStatus.NOT_FOUND, "error", "Event not found"));
        // Menyimpan file
        if(!banner.isEmpty()){
            if(banner.getContentType() == "image/jpeg"){
                try{
//                   Delete File lama;
                    Path pathOld = Path.of(uploadPath+"/banner/" + event.getBanner());
                    Files.delete(pathOld);
                    log.info("berhasil menghapus file  file ");

//                    Menambahkan file baru
                    String extension = banner.getOriginalFilename().substring(banner.getOriginalFilename().lastIndexOf("."));

                    String newFileName = UUID.randomUUID().toString()+extension;

                    Path path = Path.of("upload/public/banner/" + newFileName);

                    Files.write(path, banner.getBytes());

                    log.info("berhasil menyimpan file");

                    event.setBanner(newFileName);

                }catch (IOException e){

                }
            }else{
                throw new ErrorResponseException(HttpStatus.BAD_REQUEST,"error", "file not image" );
            }
        }

        List<Category> categories = categoryRepository.findAllById(request.getCategories().stream().map(CategoriesRequest::getCategoriesId).toList());
        event.setCategories(categories);

        LocationEvent locationEvent = new LocationEvent();
        locationEvent.setCity(request.getLocation().getCity());
        locationEvent.setProvince(request.getLocation().getCity());
        locationEvent.setAddres(request.getLocation().getAddres());
        locationEvent.setLatitude(request.getLocation().getLatitude());
        locationEvent.setLongitude(request.getLocation().getLongitude());


    }

    public List<ListEventResponse> getListEvent(String query){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User user = (User) authentication.getPrincipal();
        List<Event> events = new ArrayList<>();
        if(query == null || query.trim().isEmpty()){
            events = eventRepository.findAllByUser_id(user.getId());
        }else{
            events = eventRepository.fulltextSearchAndByuserId(query, user.getId());
        }


        List<ListEventResponse> responses = new ArrayList<>();
        for(Event event : events){
            ListEventResponse response = new ListEventResponse();
            response.setId(event.getId());
            response.setBaner(event.getBanner());
            response.setTitle(event.getTitle());
            response.setStatus(event.getStatus().name());

            LocationEventResponse locationResponse = new LocationEventResponse();
            locationResponse.setCity(event.getLocation().getCity());
            locationResponse.setProvince(event.getLocation().getProvince());
            locationResponse.setAddres(event.getLocation().getAddres());
            locationResponse.setLatitude(event.getLocation().getLatitude());
            locationResponse.setLongitude(event.getLocation().getLongitude());

            response.setLocation(locationResponse);

            DateTimeFormatter format = DateTimeFormatter.ofPattern("dd MMMM yyyy", new Locale("id", "ID"));
            DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm", new Locale("id", "ID"));
            response.setStartDate(event.getStartEvent().format(format));
            response.setEndDate(event.getEndEvent().format(format));
            response.setStartTime((event.getStartEvent().format(timeFormatter)));
            response.setEndTime((event.getEndEvent().format(timeFormatter)));

            EventManagerResponse eventManagerResponse = new EventManagerResponse();
            eventManagerResponse.setName(event.getUser().getName());
            eventManagerResponse.setId(event.getUser().getId());
            eventManagerResponse.setEmail(event.getUser().getEmail());

            response.setEventManager(eventManagerResponse);

            responses.add(response);

        }

        return responses;
    }


    public WebResponseList<List<ListEventResponse>> searchEventUser(String date, String category, String q, Integer page, Integer size){

        if(size < 1){
            throw new ErrorResponseException(HttpStatus.BAD_REQUEST, "error", "size must greather than zero");
        }

        StringBuilder queryBuilder = new StringBuilder("""
        SELECT e.*
        FROM events e
        JOIN categories_events ce ON e.id = ce.event_id
        JOIN categories c ON ce.category_id = c.id
        WHERE 1=1 AND  (e.status = 'ACTIVE' OR e.status='SCHEDULED') 
    """);

        List<String> queryParams = new ArrayList<>();

        if (q != null && !q.trim().isEmpty()) {
            queryBuilder.append(" AND e.title @@ to_tsquery(:q)");
            queryParams.add("q");
        }

        if (category != null && !category.trim().isEmpty()) {
            queryBuilder.append(" AND c.name = :category");
            queryParams.add("category");
        }

        if (date != null && !date.trim().isEmpty()) {
            queryBuilder.append(" AND DATE(e.start_event) = :date");
            queryParams.add("date");
        }

        String countQuery = queryBuilder.toString().replace("e.*", "COUNT(e.id)");
        Query count = entityManager.createNativeQuery(countQuery, Long.class);

        queryBuilder.append(" ORDER BY name ASC LIMIT :size OFFSET :page");
        Query nativeQuery = entityManager.createNativeQuery(queryBuilder.toString(), Event.class);
        nativeQuery.setParameter("size", size);
        nativeQuery.setParameter("page", page*size);

        if (queryParams.contains("q")) {
            nativeQuery.setParameter("q", q);
            count.setParameter("q", q);
        }
        if (queryParams.contains("category")) {
            nativeQuery.setParameter("category", category);
            count.setParameter("category", category);
        }
        if (queryParams.contains("date")) {
            LocalDate parse = LocalDate.parse(date);
            nativeQuery.setParameter("date", parse);
            count.setParameter("date", parse);
        }

        Long countEvent = (Long) count.getSingleResult();
        int totalPage = (int) Math.ceil(countEvent/size.doubleValue());
        if(page > totalPage){
            throw new ErrorResponseException(HttpStatus.BAD_REQUEST, "error", "page must greather than totalPage");
        }

        List<Event> events = nativeQuery.getResultList();

//        Meta data pagination
        MetaData metaData = new MetaData();
        metaData.setPage(page);
        metaData.setSize(size);
        metaData.setTotalPages(totalPage);
        metaData.setTotalElements(countEvent);
        metaData.setHasNext((page != metaData.getTotalPages()-1) && (page <= metaData.getTotalPages()-1 ));
        metaData.setHasPrevious((page != 0 ) && (page >= 0 ));

        List<ListEventResponse> responses = new ArrayList<>();

        for(Event event : events){
            ListEventResponse response = new ListEventResponse();
            response.setId(event.getId());
            response.setBaner(event.getBanner());
            response.setTitle(event.getTitle());
            response.setStatus(event.getStatus().name());

            LocationEventResponse locationResponse = new LocationEventResponse();
            locationResponse.setCity(event.getLocation().getCity());
            locationResponse.setProvince(event.getLocation().getProvince());
            locationResponse.setAddres(event.getLocation().getAddres());
            locationResponse.setLatitude(event.getLocation().getLatitude());
            locationResponse.setLongitude(event.getLocation().getLongitude());

            response.setLocation(locationResponse);

            DateTimeFormatter format = DateTimeFormatter.ofPattern("dd MMMM yyyy", new Locale("id", "ID"));
            DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm", new Locale("id", "ID"));
            response.setStartDate(event.getStartEvent().format(format));
            response.setEndDate(event.getEndEvent().format(format));
            response.setStartTime((event.getStartEvent().format(timeFormatter)));
            response.setEndTime((event.getEndEvent().format(timeFormatter)));

            EventManagerResponse eventManagerResponse = new EventManagerResponse();
            eventManagerResponse.setName(event.getUser().getName());
            eventManagerResponse.setId(event.getUser().getId());
            eventManagerResponse.setEmail(event.getUser().getEmail());

            response.setEventManager(eventManagerResponse);

            responses.add(response);

        }

        return WebResponseList
                .<List<ListEventResponse>>builder()
                .data(responses)
                .meta(metaData)
                .status("success")
                .message("success get events")
                .build();

    }

    public EventResponse getEventByid(Integer id){
        Event event = eventRepository.findById(id)
                .orElseThrow(() -> new ErrorResponseException(HttpStatus.NOT_FOUND, "error", "Event not found"));
        LocationEventResponse locationResponse =  LocationEventResponse
                .builder()
                .city(event.getLocation().getCity())
                .province(event.getLocation().getProvince())
                .addres(event.getLocation().getAddres())
                .longitude(event.getLocation().getLongitude())
                .latitude(event.getLocation().getLatitude())
                .build();

        List<TicketResponse> ticketResponses = new ArrayList<>();
        event.getTickets().forEach(ticket -> {
            TicketResponse response = new TicketResponse();
            response.setId(ticket.getId());
            response.setTitle(ticket.getTitle());
            response.setPrice(ticket.getPrice());
            response.setStartTime(ticket.getStartTicket());
            response.setEndTime(ticket.getEndTicket());
            response.setDescription(ticket.getDescription());
            response.setQuantity(ticket.getQuantity());
            response.setIsFree(ticket.getIsFree());
            ticketResponses.add(response);
        });

        List<String> categoriesResponse = new ArrayList<>();

        for (Category category : event.getCategories()){
            categoriesResponse.add(category.getName());
        }

        return EventResponse
                .builder()
                .id(event.getId())
                .title(event.getTitle())
                .banner(event.getBanner())
                .status(event.getStatus().name())
                .categories(categoriesResponse)
                .location(locationResponse)
                .tickets(ticketResponses)
                .description(event.getDescription())
                .violation(event.getViolation())
                .startTime(event.getStartEvent())
                .endTime(event.getEndEvent())
                .createdAt(event.getCreatedAt())
                .updatedAt(event.getUpdatedAt())
                .build();

    }

    public List<Event> getEventStatusToActive(){
        return eventRepository.findByStatusAndStartEventBefore(StatusEvent.SCHEDULED, LocalDateTime.now());
    }

    public void updateStatusToActive(Event event){
        event.setStatus(StatusEvent.ACTIVE);
        eventRepository.save(event);
    }

    public List<Event> getEventStatusToCompleted(){
        return eventRepository.findByStatusAndEndEventBefore(StatusEvent.ACTIVE, LocalDateTime.now());
    }

    public void updateStatusToCompleted(Event event){
        event.setStatus(StatusEvent.COMPLETED);
        eventRepository.save(event);
    }


}
