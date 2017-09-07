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

package info.gianlucacosta.mytasks.model.tasks;

import java.util.Objects;
import java.util.UUID;

/**
 * Entry in a task list.
 * <p>
 * Its title is always trimmed and, after that, cannot be empty.
 */
public class Task {
    private final UUID id;

    private final String title;

    private final boolean done;


    public Task(String title) {
        this(
                UUID.randomUUID(),
                title,
                false
        );
    }

    public Task(UUID id, String title, boolean done) {
        Objects.requireNonNull(id);
        Objects.requireNonNull(title);

        this.id = id;
        this.title = title.trim();

        if (this.title.isEmpty()) {
            throw new IllegalArgumentException("The title cannot be empty");
        }

        this.done = done;
    }


    public UUID getId() {
        return id;
    }


    public String getTitle() {
        return title;
    }


    public boolean isDone() {
        return done;
    }


    public Task copy(String title, Boolean done) {
        return new Task(
                id,
                title,
                done
        );
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Task task = (Task) o;

        if (done != task.done) return false;
        if (id != null ? !id.equals(task.id) : task.id != null) return false;
        return title != null ? title.equals(task.title) : task.title == null;
    }

    @Override
    public int hashCode() {
        int result = id != null ? id.hashCode() : 0;
        result = 31 * result + (title != null ? title.hashCode() : 0);
        result = 31 * result + (done ? 1 : 0);
        return result;
    }
}
