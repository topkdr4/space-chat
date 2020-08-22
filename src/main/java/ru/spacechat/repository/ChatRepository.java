package ru.spacechat.repository;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import ru.spacechat.commons.JdbcErrors;
import ru.spacechat.model.ChatInfo;
import ru.spacechat.model.ChatMember;

import javax.sql.DataSource;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Types;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;





@Repository
public class ChatRepository {

    @Autowired
    private DataSource dataSource;



    public List<ChatInfo> getChatList(String login) {
        try {
            List<ChatInfo> result = new ArrayList<>();

            List<String> ids = getChatListId(login);
            for (String id : ids) {
                ChatInfo item = new ChatInfo();
                item.setId(id);
                item.setName(getChatName(id, login));
                item.setMembers(getChatMembers(id));

                result.add(item);
            }

            return result;
        } catch (Exception e) {
            throw JdbcErrors.rethrow(e);
        }
    }



    public ChatInfo createGroupChat(String login, String name, Collection<String> members) {
        try {

            return null;
        } catch (Exception e) {
            throw JdbcErrors.rethrow(e);
        }
    }



    public ChatInfo createFriendChat(String login, String friend) {
        try (Connection connection = dataSource.getConnection()) {
            connection.setAutoCommit(true);

            int index = 1;
            CallableStatement stmt = connection.prepareCall("{? = call create_dialog_user(?, ?)}");
            stmt.registerOutParameter(index++, Types.VARCHAR);
            stmt.setString(index++, login);
            stmt.setString(index++, friend);

            stmt.execute();

            String id = stmt.getString(1);

            ChatInfo info = new ChatInfo();
            info.setId(id);
            info.setName(getChatName(id, login));
            info.setMembers(getChatMembers(id));

            return info;
        } catch (Exception e) {
            throw JdbcErrors.rethrow(e);
        }
    }



    private String getChatName(String id, String login) {
        try (Connection connection = dataSource.getConnection()) {
            connection.setAutoCommit(false);

            int index = 1;
            CallableStatement stmt = connection.prepareCall("{? = call get_chat_name(?, ?)}");
            stmt.registerOutParameter(index++, Types.VARCHAR);
            stmt.setString(index++, id);
            stmt.setString(index++, login);

            stmt.execute();

            return stmt.getString(1);
        } catch (Exception e) {
            throw JdbcErrors.rethrow(e);
        }
    }


    private List<ChatMember> getChatMembers(String id) {
        try (Connection connection = dataSource.getConnection()) {
            connection.setAutoCommit(false);

            int index = 1;
            CallableStatement stmt = connection.prepareCall("{? = call get_chat_members(?)}");
            stmt.registerOutParameter(index++, Types.OTHER);
            stmt.setString(index++, id);

            stmt.execute();

            ResultSet set = (ResultSet) stmt.getObject(1);

            List<ChatMember> result = new ArrayList<>();

            while (set.next()) {
                ChatMember item = new ChatMember();
                item.setLogin(set.getString("login"));
                item.setName(set.getString("name"));

                result.add(item);
            }

            return result;
        } catch (Exception e) {
            throw JdbcErrors.rethrow(e);
        }
    }


    private List<String> getChatListId(String login) {
        try (Connection connection = dataSource.getConnection()) {
            connection.setAutoCommit(false);

            int index = 1;
            CallableStatement stmt = connection.prepareCall("{? = call get_chat_list_id(?)}");
            stmt.registerOutParameter(index++, Types.OTHER);
            stmt.setString(index++, login);

            stmt.execute();

            ResultSet set = (ResultSet) stmt.getObject(1);

            List<String> result = new ArrayList<>();

            while (set.next()) {
                result.add(set.getString("id"));
            }

            return result;
        } catch (Exception e) {
            throw JdbcErrors.rethrow(e);
        }
    }

}
