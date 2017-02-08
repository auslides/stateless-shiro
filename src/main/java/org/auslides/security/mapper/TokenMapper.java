package org.auslides.security.mapper;

import org.auslides.security.model.DBAuthenticationToken;
import org.apache.ibatis.annotations.*;

import java.util.List;

public interface TokenMapper {

    final String getAll = "SELECT * FROM T_TOKEN";
    final String getById = "SELECT * FROM T_TOKEN WHERE ID = #{id}";
    final String getByToken = "SELECT * FROM T_TOKEN WHERE token = #{token}";
    final String deleteByToken = "DELETE from T_TOKEN WHERE token = #{token}";
    final String deleteAll = "DELETE from T_TOKEN WHERE";
    final String insert = "INSERT INTO T_TOKEN (email, token) VALUES (#{email}, #{token})";
    final String updateToken = "UPDATE T_TOKEN SET token = #{token} WHERE token = #{token}";

    @Select(getAll)
    @Results(value = {
            @Result(property = "id", column = "ID"),
            @Result(property = "email", column = "email"),
            @Result(property = "token", column = "token")
    })
    List<DBAuthenticationToken> getAll();

    @Select(getById)
    @Results(value = {
            @Result(property = "id", column = "ID"),
            @Result(property = "email", column = "email"),
            @Result(property = "token", column = "token")
    })
    DBAuthenticationToken getById(int id);

    @Select(getByToken)
    @Results(value = {
            @Result(property = "id", column = "ID"),
            @Result(property = "email", column = "email"),
            @Result(property = "token", column = "token")
    })
    DBAuthenticationToken getByToken(String token);

    @Update(updateToken)
    void updateToken(String token);

    @Delete(deleteByToken)
    void deleteByToken(String token);

    @Delete(deleteAll)
    void deleteAll();

    @Insert(insert)
    @Options(useGeneratedKeys = true, keyProperty = "id")
    void insert(DBAuthenticationToken dbAuthenticationToken) ;
}