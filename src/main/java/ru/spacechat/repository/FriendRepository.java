package ru.spacechat.repository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import ru.spacechat.commons.JdbcErrors;
import ru.spacechat.model.AvailableFriend;
import ru.spacechat.model.Friend;
import ru.spacechat.model.FriendStatus;

import javax.sql.DataSource;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Types;





@Repository
public class FriendRepository {

    @Autowired
    private DataSource dataSource;



    public SearchResp<Friend> getFriendList(String login, SearchReqt searchReqt) {
        try (Connection connection = dataSource.getConnection()) {
            connection.setAutoCommit(false);

            int index = 1;

            CallableStatement stmt = connection.prepareCall("{? = call get_friend_list(?, ?, ?)}");
            stmt.registerOutParameter(index++, Types.OTHER);
            stmt.setString(index++, login);
            stmt.setInt(index++, searchReqt.getOffset());
            stmt.setInt(index++, searchReqt.getLimit());

            stmt.execute();

            ResultSet set = (ResultSet) stmt.getObject(1);

            SearchResp<Friend> result = new SearchResp<>();
            result.setCurrentPage(searchReqt.getCurrentPage());
            result.setShowPerPage(searchReqt.getShowPerPage());

            while (set.next()) {
                Friend item = new Friend();
                item.setLogin(set.getString("login"));
                item.setName(set.getString("name"));

                result.getData().add(item);
                result.setItemCount(set.getInt("itemCount"));
            }

            return result;
        } catch (Exception e) {
            throw JdbcErrors.rethrow(e);
        }
    }


    public SearchResp<AvailableFriend> searchFriend(String login, SearchReqt searchReqt) {
        try (Connection connection = dataSource.getConnection()) {
            connection.setAutoCommit(false);

            int index = 1;

            CallableStatement stmt = connection.prepareCall("{? = call search_friend(?, ?, ?)}");
            stmt.registerOutParameter(index++, Types.OTHER);
            stmt.setString(index++, login);
            stmt.setInt(index++, searchReqt.getOffset());
            stmt.setInt(index++, searchReqt.getLimit());

            stmt.execute();

            ResultSet set = (ResultSet) stmt.getObject(1);

            SearchResp<AvailableFriend> result = new SearchResp<>();
            result.setCurrentPage(searchReqt.getCurrentPage());
            result.setShowPerPage(searchReqt.getShowPerPage());

            while (set.next()) {
                AvailableFriend item = new AvailableFriend();
                item.setLogin(set.getString("login"));
                item.setName(set.getString("name"));
                item.setStatus(FriendStatus.valueOf(set.getString("status")));
                item.setMessage(set.getString("message"));

                result.getData().add(item);
                result.setItemCount(set.getInt("itemCount"));
            }

            return result;
        } catch (Exception e) {
            throw JdbcErrors.rethrow(e);
        }
    }



    public SearchResp<Friend> getIncomingFriendList(String login, SearchReqt searchReqt) {
        try (Connection connection = dataSource.getConnection()) {
            connection.setAutoCommit(false);

            int index = 1;

            CallableStatement stmt = connection.prepareCall("{? = call get_incoming_friend(?, ?, ?)}");
            stmt.registerOutParameter(index++, Types.OTHER);
            stmt.setString(index++, login);
            stmt.setInt(index++, searchReqt.getOffset());
            stmt.setInt(index++, searchReqt.getLimit());

            stmt.execute();

            ResultSet set = (ResultSet) stmt.getObject(1);

            SearchResp<Friend> result = new SearchResp<>();
            result.setCurrentPage(searchReqt.getCurrentPage());
            result.setShowPerPage(searchReqt.getShowPerPage());

            while (set.next()) {
                Friend item = new Friend();
                item.setLogin(set.getString("login"));
                item.setName(set.getString("name"));

                result.getData().add(item);
                result.setItemCount(set.getInt("itemCount"));
            }

            return result;
        } catch (Exception e) {
            throw JdbcErrors.rethrow(e);
        }
    }



    public SearchResp<Friend> getRequestedFriendList(String login, SearchReqt searchReqt) {
        try (Connection connection = dataSource.getConnection()) {
            connection.setAutoCommit(false);

            int index = 1;

            CallableStatement stmt = connection.prepareCall("{? = call get_requested_friend(?, ?, ?)}");
            stmt.registerOutParameter(index++, Types.OTHER);
            stmt.setString(index++, login);
            stmt.setInt(index++, searchReqt.getOffset());
            stmt.setInt(index++, searchReqt.getLimit());

            stmt.execute();

            ResultSet set = (ResultSet) stmt.getObject(1);

            SearchResp<Friend> result = new SearchResp<>();
            result.setCurrentPage(searchReqt.getCurrentPage());
            result.setShowPerPage(searchReqt.getShowPerPage());

            while (set.next()) {
                Friend item = new Friend();
                item.setLogin(set.getString("login"));
                item.setName(set.getString("name"));

                result.getData().add(item);
                result.setItemCount(set.getInt("itemCount"));
            }

            return result;
        } catch (Exception e) {
            throw JdbcErrors.rethrow(e);
        }
    }



    public void addFriend(String user, String friend) {
        try (Connection connection = dataSource.getConnection()) {
            connection.setAutoCommit(true);

            int index = 1;

            CallableStatement stmt = connection.prepareCall("{call add_friend(?, ?)}");
            stmt.setString(index++, user);
            stmt.setString(index++, friend);

            stmt.execute();

        } catch (Exception e) {
            throw JdbcErrors.rethrow(e);
        }
    }



    public void removeFriend(String user, String friend) {
        try (Connection connection = dataSource.getConnection()) {
            connection.setAutoCommit(true);

            int index = 1;

            CallableStatement stmt = connection.prepareCall("{call remove_friend(?, ?)}");
            stmt.setString(index++, user);
            stmt.setString(index++, friend);

            stmt.execute();

        } catch (Exception e) {
            throw JdbcErrors.rethrow(e);
        }
    }



    public void cancelRequest(String login, String friend) {
        try (Connection connection = dataSource.getConnection()) {
            connection.setAutoCommit(true);

            int index = 1;

            CallableStatement stmt = connection.prepareCall("{call cancel_request(?, ?)}");
            stmt.setString(index++, login);
            stmt.setString(index++, friend);

            stmt.execute();

        } catch (Exception e) {
            throw JdbcErrors.rethrow(e);
        }
    }



    public void abortRequest(String login, String friend) {
        try (Connection connection = dataSource.getConnection()) {
            connection.setAutoCommit(true);

            int index = 1;

            CallableStatement stmt = connection.prepareCall("{call abort_request(?, ?)}");
            stmt.setString(index++, login);
            stmt.setString(index++, friend);

            stmt.execute();

        } catch (Exception e) {
            throw JdbcErrors.rethrow(e);
        }
    }



    public void acceptRequest(String login, String friend) {
        try (Connection connection = dataSource.getConnection()) {
            connection.setAutoCommit(true);

            int index = 1;

            CallableStatement stmt = connection.prepareCall("{call accept_request(?, ?)}");
            stmt.setString(index++, login);
            stmt.setString(index++, friend);

            stmt.execute();

        } catch (Exception e) {
            throw JdbcErrors.rethrow(e);
        }
    }

}
