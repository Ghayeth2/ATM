package com.atm.business.abstracts;

import java.util.List;

public interface CRUDServices <T, U>{
    String save(T dto);
    String update(U dto, String slug);
    String delete(String slug);
    // For backend usage only
    T findBySlug (String slug);
    List<T> findAll();
}
