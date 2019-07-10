package com.Fate_Project.Dao;

import com.Fate_Project.Entity.Terminal;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface TerminalDao {
    List<Terminal> Select_All();
    void Updata_tuser_byTid(int tid,String newtuser);
    Terminal Select_byTid(int tid);
    void Updata_tstate_byTid(int tid,int tstate);
    void Updata_wstate_byTid(int tid,int wstate);
    void Updata_astate_byTid(int tid,int astate);
    void Updata_dstate_byTid(int tid,int dstate);
    void Updata_phstate_byTid(int tid,int phstate);
    void Updata_wmax_byTid(int tid,String  wmax);
    void Updata_tmax_byTid(int tid,String tmax);
    void Updata_hmax_byTid(int tid,String hmax);
    void Updata_amax_byTid(int tid,String amax);
    void Updata_watempmax_byTid(int tid,String watempmax);
    void Updata_dmax_byTid(int tid,String dmax);
    void Updata_phtempmax_byTid(int tid,String phtempmax);
    void Updata_phmax_byTid(int tid,String phmax);
    void Updata_phmvmax_byTid(int tid,String phmvmax);
    void Updata_warn_byTid(int tid,String warn);
}
