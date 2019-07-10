package com.Fate_Project.Dao;

import com.Fate_Project.Entity.Email;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface EmailDao {
    Email Select_By_Email(String email);
    void ReplaceEmail(Email email);
}
