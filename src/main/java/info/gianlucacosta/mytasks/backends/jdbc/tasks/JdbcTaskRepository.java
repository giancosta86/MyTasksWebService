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

package info.gianlucacosta.mytasks.backends.jdbc.tasks;

import info.gianlucacosta.mytasks.backends.Persistent;
import info.gianlucacosta.mytasks.model.tasks.Task;
import info.gianlucacosta.mytasks.model.tasks.TaskRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * Task repository backed by JDBC
 */
@Repository
@Persistent
public class JdbcTaskRepository implements TaskRepository {
    @Autowired
    private NamedParameterJdbcTemplate jdbcTemplate;


    @Override
    public List<Task> findByUserName(String userName) {
        Map<String, Object> sqlParams = new HashMap<>();
        sqlParams.put("userName", userName);


        return jdbcTemplate.query(
                "SELECT id, title, done FROM Tasks WHERE userName = :userName",
                sqlParams,
                (resultSet, rowNumber) -> new Task(
                        resultSet.getObject("id", UUID.class),
                        resultSet.getString("title"),
                        resultSet.getBoolean("done")
                )
        );
    }


    @Transactional
    @Override
    public void add(String userName, Task task) {
        Map<String, Object> sqlParams = new HashMap<>();
        sqlParams.put("userName", userName);
        sqlParams.put("id", task.getId());
        sqlParams.put("title", task.getTitle());
        sqlParams.put("done", task.isDone());


        jdbcTemplate.update(
                "INSERT INTO Tasks(userName, id, title, done) VALUES (:userName, :id, :title, :done)",
                sqlParams
        );
    }


    @Transactional
    @Override
    public void update(Task task) {
        Map<String, Object> sqlParams = new HashMap<>();
        sqlParams.put("id", task.getId());
        sqlParams.put("title", task.getTitle());
        sqlParams.put("done", task.isDone());

        jdbcTemplate.update(
                "UPDATE Tasks SET title = :title, done = :done WHERE id = :id",
                sqlParams
        );
    }


    @Transactional
    @Override
    public void removeById(UUID taskId) {
        Map<String, Object> sqlParams = new HashMap<>();
        sqlParams.put("id", taskId);

        jdbcTemplate.update(
                "DELETE FROM Tasks WHERE id = :id",
                sqlParams
        );
    }
}
