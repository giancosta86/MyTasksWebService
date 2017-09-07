/*^
  ===========================================================================
  MyTasksWebService
  ===========================================================================
  Copyright (C) 2017 Gianluca Costa
  ===========================================================================
  Licensed under the Apache License, Version 2.0 (the "License");
  you may not use this file except in compliance with the License.
  You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

  Unless required by applicable law or agreed to in writing, software
  distributed under the License is distributed on an "AS IS" BASIS,
  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
  See the License for the specific language governing permissions and
  limitations under the License.
  ===========================================================================
*/

package info.gianlucacosta.mytasks.backends.jdbc.users;

import info.gianlucacosta.mytasks.backends.Persistent;
import info.gianlucacosta.mytasks.model.users.User;
import info.gianlucacosta.mytasks.model.users.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

/**
 * User repository backed by JDBC
 */
@Repository
@Persistent
public class JdbcUserRepository implements UserRepository {
    @Autowired
    private NamedParameterJdbcTemplate jdbcTemplate;


    @Override
    public List<User> findAll() {
        return jdbcTemplate
                .query(
                        "SELECT name, password FROM Users ORDER BY name",
                        Collections.emptyMap(),
                        (resultSet, rowNumber) ->
                                new User(
                                        resultSet.getString("name"),
                                        resultSet.getString("password")
                                )
                );
    }


    @Override
    public Optional<User> findByName(String name) {
        Map<String, Object> sqlParams = new HashMap<>();
        sqlParams.put("name", name);

        try {
            return Optional.of(
                    jdbcTemplate
                            .queryForObject(
                                    "SELECT name, password FROM Users WHERE name = :name",
                                    sqlParams,
                                    (resultSet, rowNumber) ->
                                            new User(
                                                    resultSet.getString("name"),
                                                    resultSet.getString("password")
                                            )
                            )
            );
        } catch (EmptyResultDataAccessException ex) {
            return Optional.empty();
        }
    }


    @Transactional
    @Override
    public void add(User user) {
        Map<String, Object> sqlParams = new HashMap<>();
        sqlParams.put("name", user.getName());
        sqlParams.put("password", user.getPassword());

        jdbcTemplate.update(
                "INSERT INTO Users(name, password) VALUES (:name, :password)",
                sqlParams
        );
    }


    @Transactional
    @Override
    public void update(User user) {
        Map<String, Object> sqlParams = new HashMap<>();
        sqlParams.put("name", user.getName());
        sqlParams.put("password", user.getPassword());

        jdbcTemplate.update(
                "UPDATE Users SET password = :password WHERE name = :name",
                sqlParams
        );
    }


    @Transactional
    @Override
    public void removeByName(String userName) {
        if (Objects.equals(User.ADMIN_NAME, userName)) {
            throw new IllegalArgumentException("Cannot remove the administrative user");
        }

        Map<String, Object> sqlParams = new HashMap<>();
        sqlParams.put("name", userName);

        jdbcTemplate.update(
                "DELETE FROM Users WHERE name = :name",
                sqlParams
        );
    }
}
