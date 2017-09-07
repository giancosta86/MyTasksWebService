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


import info.gianlucacosta.mytasks.MvcTestBase;
import info.gianlucacosta.mytasks.model.tasks.Task;
import info.gianlucacosta.mytasks.model.tasks.TaskRepository;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;

import javax.servlet.ServletException;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@RunWith(SpringRunner.class)
@WebMvcTest(TaskController.class)
public class TaskControllerTest extends MvcTestBase {
    @MockBean
    private TaskRepository taskRepository;

    private final List<Task> adminTasks = Arrays.asList(
            new Task("Admin task 1"),
            new Task("Admin task 2")
    );


    private final List<Task> standardTasks = Arrays.asList(
            new Task("Standard task 1"),
            new Task("Stamdard task 2")
    );


    @Override
    public void init() {
        super.init();

        given(
                taskRepository.findByUserName(adminUser.getName())
        )
                .willReturn(adminTasks);

        given(
                taskRepository.findByUserName(standardUser.getName())
        )
                .willReturn(standardTasks);
    }


    @Test
    public void listingTaskShouldShowOnlyTheTasksOfTheLoggedUser() throws Exception {
        this.mvc.perform(
                get(TaskController.path + "/")
                        .header(
                                "Authorization",
                                getBasicAuthenticationHeader(standardUser)
                        )
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json(
                        jsonMapper.writeValueAsString(standardTasks)
                        )
                );

        verify(taskRepository).findByUserName(standardUser.getName());
    }


    @Test
    public void addingTasksShouldAddTasksToTheCurrentUser() throws Exception {
        String newTitle = "Brand-new task";

        this.mvc.perform(
                post(TaskController.path + "/")
                        .header(
                                "Authorization",
                                getBasicAuthenticationHeader(standardUser)
                        )
                        .param("title", newTitle)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated());

        ArgumentCaptor<Task> addedTaskCaptor =
                ArgumentCaptor.forClass(Task.class);

        verify(taskRepository).add(
                Mockito.eq(standardUser.getName()),
                addedTaskCaptor.capture()
        );


        Task addedTask =
                addedTaskCaptor.getValue();


        assertThat(
                addedTask.getTitle(),
                is(newTitle)
        );
    }


    @Test
    public void addingTasksShouldReturnTheNewTask() throws Exception {
        String newTitle = "Brand-new task";

        this.mvc.perform(
                post(TaskController.path + "/")
                        .header(
                                "Authorization",
                                getBasicAuthenticationHeader(standardUser)
                        )
                        .param("title", newTitle)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.title", is(newTitle)))
                .andExpect(jsonPath("$.done", is(false)));
    }


    @Test(expected = ServletException.class)
    public void addingTasksWhoseTitleContainsOnlySpacesShouldFail() throws Exception {
        this.mvc.perform(
                post(TaskController.path + "/")
                        .header(
                                "Authorization",
                                getBasicAuthenticationHeader(standardUser)
                        )
                        .param("title", "       ")
                        .accept(MediaType.APPLICATION_JSON));
    }


    @Test
    public void editingTasksForTheLoggedUserShouldWork() throws Exception {
        Task replacingTask =
                standardTasks
                        .stream()
                        .findFirst()
                        .get()
                        .copy("New title", true);


        this.mvc.perform(
                put(TaskController.path + "/")
                        .header(
                                "Authorization",
                                getBasicAuthenticationHeader(standardUser)
                        )
                        .param("id", replacingTask.getId().toString())
                        .param("title", replacingTask.getTitle())
                        .param("done", Boolean.toString(replacingTask.isDone()))
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());

        verify(taskRepository)
                .update(replacingTask);
    }


    @Test
    public void editingTasksBelongingToOtherUsersShouldBeForbidden() throws Exception {
        Task replacingTask =
                adminTasks
                        .stream()
                        .findFirst()
                        .get()
                        .copy("New title", true);


        this.mvc.perform(
                put(TaskController.path + "/")
                        .header(
                                "Authorization",
                                getBasicAuthenticationHeader(standardUser)
                        )
                        .param("id", replacingTask.getId().toString())
                        .param("title", replacingTask.getTitle())
                        .param("done", Boolean.toString(replacingTask.isDone()))
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden());

        verify(taskRepository, times(0))
                .update(replacingTask);
    }


    @Test
    public void removingTasksForTheLoggedUserShouldWork() throws Exception {
        UUID taskIdToRemove =
                standardTasks
                        .stream()
                        .findFirst()
                        .get()
                        .getId();


        this.mvc.perform(
                delete(TaskController.path + "/")
                        .header(
                                "Authorization",
                                getBasicAuthenticationHeader(standardUser)
                        )
                        .param("id", taskIdToRemove.toString())
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());

        verify(taskRepository)
                .removeById(taskIdToRemove);
    }


    @Test
    public void removingTasksBelongingToOtherUsersShouldBeForbidden() throws Exception {
        UUID taskIdToRemove =
                adminTasks
                        .stream()
                        .findFirst()
                        .get()
                        .getId();


        this.mvc.perform(
                delete(TaskController.path + "/")
                        .header(
                                "Authorization",
                                getBasicAuthenticationHeader(standardUser)
                        )
                        .param("id", taskIdToRemove.toString())
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden());

        verify(taskRepository, times(0))
                .removeById(taskIdToRemove);
    }
}
