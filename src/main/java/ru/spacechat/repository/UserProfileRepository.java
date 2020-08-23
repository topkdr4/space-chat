package ru.spacechat.repository;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import ru.spacechat.commons.JdbcErrors;
import ru.spacechat.commons.OperationException;
import ru.spacechat.model.UserProfile;

import javax.annotation.PostConstruct;
import javax.sql.DataSource;
import java.sql.*;
import java.util.concurrent.TimeUnit;





@Repository
public class UserProfileRepository {



    @Autowired
    private DataSource dataSource;


    private final LoadingCache<String, byte[]> imageCache = buildCache();


    public LoadingCache<String, byte[]> buildCache() {
        return CacheBuilder.newBuilder()
                .maximumSize(100)
                .expireAfterWrite(10, TimeUnit.MINUTES)
                .build(new CacheLoader<String, byte[]>() {
                    @Override
                    public byte[] load(String login) {
                        return getUserAvatar(login);
                    }
                });
    }



    public UserProfile getUserProfile(String login) {
        try (Connection connection = dataSource.getConnection()) {
            connection.setAutoCommit(false);

            int index = 1;

            CallableStatement stmt = connection.prepareCall("{? = call get_user_profile(?)}");
            stmt.registerOutParameter(index++, Types.OTHER);
            stmt.setString(index++, login);

            stmt.execute();

            ResultSet set = (ResultSet) stmt.getObject(1);

            if (!set.next())
                throw new OperationException("Профиль пользователя не найден");

            Timestamp timestamp = set.getTimestamp("birth");

            UserProfile result = new UserProfile();
            result.setName(set.getString("name"));
            result.setBirth(timestamp == null ? null : timestamp.getTime());
            result.setStatus(set.getString("status"));
            result.setPhone(set.getString("phone"));
            result.setCity(set.getString("city"));
            result.setAboutMe(set.getString("about_me"));
            result.setSex(set.getBoolean("sex"));

            return result;
        } catch (Exception e) {
            throw JdbcErrors.rethrow(e);
        }
    }


    public void saveUserProfile(String login, UserProfile profile) {
        try (Connection connection = dataSource.getConnection()) {

            connection.setAutoCommit(true);

            int index = 1;

            CallableStatement stmt = connection.prepareCall("{call save_user_profile(?, ?, ?, ?)}");
            stmt.setString(index++, login);
            stmt.setString(index++, profile.getName());
            stmt.setTimestamp(index++, new Timestamp(profile.getBirth()));
            stmt.setString(index++, profile.getStatus());
            stmt.setBoolean(index++, profile.isSex());
            stmt.setString(index++, profile.getCity());
            stmt.setString(index++, profile.getPhone());
            stmt.setString(index++, profile.getAboutMe());

            stmt.execute();
        } catch (Exception e) {
            throw JdbcErrors.rethrow(e);
        }
    }


    public void saveUserAvatar(String login, byte[] data) {
        try (Connection connection = dataSource.getConnection()) {

            connection.setAutoCommit(true);

            int index = 1;

            CallableStatement stmt = connection.prepareCall("{call save_user_avatar(?, ?)}");
            stmt.setString(index++, login);
            stmt.setBytes(index++, data);

            stmt.execute();

            imageCache.put(login, data);
        } catch (Exception e) {
            throw JdbcErrors.rethrow(e);
        }
    }


    @SneakyThrows
    public byte[] getAvatar(String login) {
        return imageCache.get(login);
    }


    private byte[] getUserAvatar(String login) {
        try (Connection connection = dataSource.getConnection()) {

            connection.setAutoCommit(false);

            int index = 1;

            CallableStatement stmt = connection.prepareCall("{? = call get_user_avatar(?)}");
            stmt.registerOutParameter(index++, Types.OTHER);
            stmt.setString(index++, login);

            stmt.execute();

            ResultSet set = (ResultSet) stmt.getObject(1);

            if (set.next())
                return set.getBytes("image");

            throw new OperationException("Аватар не найден");
        } catch (Exception e) {
            throw JdbcErrors.rethrow(e);
        }
    }
}
