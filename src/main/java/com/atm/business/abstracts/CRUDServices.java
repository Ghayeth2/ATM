package com.atm.business.abstracts;

import java.util.List;

public interface CRUDServices <T, U, V>{
    String save(T dto);
    String update(U dto, String slug);
    String delete(String slug);
    // For backend usage only
    V findBySlug (String slug);
    List<T> findAll();
}
