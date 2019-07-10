package com.Fate_Project.Dao;

import com.Fate_Project.Entity.User;
import org.apache.ibatis.annotations.Mapper;

import java.util.Date;
import java.util.List;

@Mapper
public interface UserDao {
    User Select_By_Uid(int uid);
    User Select_By_Uemail(String uemail);
    User Select_By_Uphone(String uphone);
    int  Insert_User(User user);
    void Update_upwd_byEmail(String uemail,String upwd);
    void Update_upwd_byPhone(String uphone,String upwd);
    void Updata_uemail_byEmail(String uemail,String newEmail);
    void Updata_uemail_byPhone(String uphone,String newEmail);
    void Updata_uphone_byEmail(String uemail,String newPhone);
    void Updata_uphone_byPhone(String uphone,String newPhone);
    void Updata_utnum_byUid(int uid,int newutnum);
    void Updata_utid_byUid(int uid,String newtid);
    void Updata_ustate_byUid(int uid,int ustate);
}
