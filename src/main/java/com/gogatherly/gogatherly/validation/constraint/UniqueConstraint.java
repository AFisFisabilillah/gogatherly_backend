package com.gogatherly.gogatherly.validation.constraint;

import com.gogatherly.gogatherly.validation.annotation.Unique;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

public class UniqueConstraint implements ConstraintValidator<Unique, String> {
    @Autowired
    private EntityManager entityManager;

    private String table;
    private String column;
    private String message;
    private Unique constraintAnnotation;

    @Override
    public void initialize(Unique constraintAnnotation) {
        table = constraintAnnotation.entity().getSimpleName();
        column = constraintAnnotation.column();
         message= constraintAnnotation.message();
    }

    @Override
    public boolean isValid(String value, ConstraintValidatorContext constraintValidatorContext) {
        TypedQuery<Long> query = entityManager.createQuery("SELECT count(u.id) FROM " + table + " u WHERE u." + column + " = " + "\""+value+"\"", Long.class);
        Long result = query.getSingleResult();

        return result == 0;
    }
}
