package cn.dustlight.oauth2.uim.mappers.v1;

import cn.dustlight.oauth2.uim.entities.v1.users.DefaultPublicUimUser;
import cn.dustlight.oauth2.uim.entities.v1.users.DefaultUimUser;
import org.apache.ibatis.annotations.*;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.Date;

@Service
@Mapper
public interface UserMapper {

    @Insert("INSERT INTO users (uid,username,password,email,nickname,gender,accountExpiredAt,credentialsExpiredAt,unlockedAt,enabled) VALUES" +
            "(#{uid},#{username},#{password},#{email},#{nickname},#{gender},#{accountExpiredAt},#{credentialsExpiredAt},#{unlockedAt},#{enabled})")
    boolean insertUser(Long uid,
                       String username,
                       String password,
                       String email,
                       String nickname,
                       int gender,
                       Date accountExpiredAt,
                       Date credentialsExpiredAt,
                       Date unlockedAt,
                       boolean enabled);

    @Select("SELECT * FROM users WHERE username=#{uoe} OR email=#{uoe}")
    @Results(id = "User", value = {
            @Result(column = "uid", property = "uid"),
            @Result(column = "uid",
                    property = "roles",
                    many = @Many(select = "cn.dustlight.oauth2.uim.mappers.v1.RoleMapper.listUserRoles"))
    })
    DefaultUimUser selectUserByUsernameOrEmail(String uoe);

    @Select("SELECT * FROM users WHERE uid=#{uid}")
    @ResultMap("User")
    DefaultUimUser selectUser(Long uid);

    @Select({"<script>SELECT * FROM users WHERE uid IN ",
            "<foreach collection='uids' item='uid' open='(' separator=',' close=')'>#{uid}</foreach>",
            "</script>"})
    Collection<DefaultPublicUimUser> selectUsersPublic(@Param("uids") Collection<Long> uids);

    @Select({"<script>SELECT * FROM users WHERE username IN ",
            "<foreach collection='usernames' item='username' open='(' separator=',' close=')'>#{username}</foreach>",
            "</script>"})
    Collection<DefaultUimUser> selectUsersByUsername(@Param("usernames") Collection<String> usernames);

    @Select({"<script>SELECT * FROM users WHERE uid IN ",
            "<foreach collection='uids' item='uid' open='(' separator=',' close=')'>#{uid}</foreach>",
            "</script>"})
    Collection<DefaultUimUser> selectUsersByUid(@Param("usernames") Collection<Long> uids);

    @Select("SELECT COUNT(uid) FROM users")
    int count();

    @Select("<script>SELECT users.* FROM users," +
            "(SELECT uid FROM users" +
            "<if test='orderBy'> ORDER BY ${orderBy}</if>" +
            "<if test='limit'> LIMIT #{limit}<if test='offset'> OFFSET #{offset}</if></if>) AS tmp " +
            "WHERE users.uid=tmp.uid</script>")
    @ResultMap("User")
    Collection<DefaultUimUser> listUsers(String orderBy, Integer offset, Integer limit);

    @Select("SELECT count(uid) FROM users WHERE MATCH (username,email,nickname) AGAINST(#{keywords})")
    int countSearch(String keywords);

    @Select("<script>SELECT users.* FROM users," +
            "(SELECT uid FROM users WHERE MATCH (username,email,nickname) AGAINST(#{keywords})" +
            "<if test='orderBy'> ORDER BY ${orderBy}</if>" +
            "<if test='limit'> LIMIT #{limit}<if test='offset'> OFFSET #{offset}</if></if>) AS tmp " +
            "WHERE users.uid=tmp.uid</script>")
    @ResultMap("User")
    Collection<DefaultUimUser> searchUsers(String keywords, String orderBy, Integer offset, Integer limit);

    @Select("<script>SELECT users.* FROM users," +
            "(SELECT uid FROM users WHERE MATCH (username,email,nickname) AGAINST(#{keywords})" +
            "<if test='orderBy'> ORDER BY ${orderBy}</if>" +
            "<if test='limit'> LIMIT #{limit}<if test='offset'> OFFSET #{offset}</if></if>) AS tmp " +
            "WHERE users.uid=tmp.uid</script>")
    Collection<DefaultPublicUimUser> searchPublicUsers(String keywords, String orderBy, Integer offset, Integer limit);
}
