package ru.spacechat.repository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import ru.spacechat.commons.JdbcErrors;
import ru.spacechat.commons.OperationException;
import ru.spacechat.model.UserProfile;

import javax.sql.DataSource;
import java.sql.*;





@Repository
public class UserProfileRepository {

    @Autowired
    private DataSource dataSource;



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

        } catch (Exception e) {
            throw JdbcErrors.rethrow(e);
        }
    }


    public byte[] getUserAvatar(String login) {
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
