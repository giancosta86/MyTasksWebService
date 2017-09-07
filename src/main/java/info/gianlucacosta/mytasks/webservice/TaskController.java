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

package info.gianlucacosta.mytasks.webservice;

import info.gianlucacosta.mytasks.model.tasks.Task;
import info.gianlucacosta.mytasks.model.tasks.TaskRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.security.Principal;
import java.util.Collection;
import java.util.UUID;

@RestController
@RequestMapping(TaskController.path)
public class TaskController {
    public static final String path = "/tasks";


    @Autowired
    TaskRepository taskRepository;


    @GetMapping("/")
    public Collection<Task> listTasks(Principal principal) {
        return taskRepository
                .findByUserName(principal.getName());
    }


    @PostMapping("/")
    @ResponseStatus(HttpStatus.CREATED)
    public Task createTask(
            @RequestParam String title,
            Principal principal
    ) {
        Task task =
                new Task(title);

        taskRepository.add(
                principal.getName(),
                task
        );

        return task;
    }


    @PutMapping("/")
    public void editTask(
            @RequestParam UUID id,
            @RequestParam String title,
            @RequestParam boolean done,

            Principal principal,
            HttpServletResponse response
    ) {
        if (taskBelongsToUser(principal.getName(), id)) {
            Task editingTask =
                    new Task(id, title, done);

            taskRepository.update(editingTask);
            response.setStatus(HttpStatus.NO_CONTENT.value());
        } else {
            response.setStatus(HttpStatus.FORBIDDEN.value());
        }
    }


    @DeleteMapping("/")
    public void removeTask(
            @RequestParam UUID id,
            Principal principal,
            HttpServletResponse response
    ) {
        if (taskBelongsToUser(principal.getName(), id)) {
            taskRepository.removeById(id);
            response.setStatus(HttpStatus.NO_CONTENT.value());
        } else {
            response.setStatus(HttpStatus.FORBIDDEN.value());
        }
    }


    private boolean taskBelongsToUser(String userName, UUID taskId) {
        return taskRepository
                .findByUserName(userName)
                .stream()
                .anyMatch(task -> task.getId().equals(taskId));
    }
}
