package ru.spacechat.repository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import ru.spacechat.commons.JdbcErrors;
import ru.spacechat.model.User;

import javax.sql.DataSource;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Types;





@Repository
public class UserRepository implements Paginated<User> {

    @Autowired
    private DataSource dataSource;


    public User getUser(String login) {
        try (Connection connection = dataSource.getConnection()) {
            connection.setAutoCommit(false);

            int index = 1;

            CallableStatement stmt = connection.prepareCall("{? = call get_user(?)}");
            stmt.registerOutParameter(index++, Types.OTHER);
            stmt.setString(index++, login);

            stmt.execute();

            ResultSet resultSet = (ResultSet) stmt.getObject(1);

            if (!resultSet.next())
                return null;

            User user = new User();
            user.setLogin(login);
            user.setPassword(resultSet.getString("password"));

            return user;
        } catch (Exception e) {
            throw JdbcErrors.rethrow(e);
        }
    }


    public void saveUser(User user) {
        try (Connection connection = dataSource.getConnection()) {
            connection.setAutoCommit(true);

            int index = 1;

            CallableStatement stmt = connection.prepareCall("{call save_user(?, ?)}");
            stmt.setString(index++, user.getLogin());
            stmt.setString(index++, user.getPassword());

            stmt.execute();
        } catch (Exception e) {
            throw JdbcErrors.rethrow(e);
        }
    }


    @Override
    public SearchResp<User> find(SearchReqt reqt) {
        throw new UnsupportedOperationException();
    }
}
