package com.Fate_Project.Dao;

import com.Fate_Project.Entity.History;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface HistoryDao {
    List<History> Select_All();
    List<History> Select_By_Tid(int tid);
}
