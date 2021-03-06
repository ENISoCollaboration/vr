/*
 * To change this license header, choose License Headers in Project Properties.
 *
 * and open the template in the editor.
 */
package net.vpc.app.vainruling.plugins.tasks.web;

import net.vpc.app.vainruling.core.web.jsf.ctrl.AbstractObjectCtrl;
import net.vpc.app.vainruling.plugins.tasks.service.model.Todo;
import net.vpc.app.vainruling.plugins.tasks.service.model.TodoCategory;
import net.vpc.app.vainruling.plugins.tasks.service.model.TodoList;
import net.vpc.app.vainruling.plugins.tasks.service.model.TodoStatus;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import javax.faces.model.SelectItem;
import java.util.ArrayList;
import java.util.List;

/**
 * @author taha.bensalah@gmail.com
 */
@Component
@Scope(value = "session")
public class TodoModel extends AbstractObjectCtrl.Model<Todo> {

    private String listName = TodoList.LABO_ACTION;
    private String title = "Titre";
    private List<Todo> todo = new ArrayList<>();
    private List<TodoText> todoText = new ArrayList<>();
    private List<Todo> inProgress = new ArrayList<>();
    private List<Todo> toVerify = new ArrayList<>();
    private List<Todo> done = new ArrayList<>();
    //        private List<Stat> statuses = new ArrayList<>();
    private List<TodoStatus> statuses = new ArrayList<>();
    private List<TodoCategory> categories = new ArrayList<>();
    private List<SelectItem> statusItems = new ArrayList<>();
    private List<SelectItem> categoryItems = new ArrayList<>();
    private Integer currentCategoryId;
    private Integer currentStatusId;

    public TodoModel() {
        setCurrent(new Todo());
    }

    public List<TodoStatus> getStatuses() {
        return statuses;
    }

    public void setStatuses(List<TodoStatus> statuses) {
        this.statuses = statuses;
    }

    public List<TodoCategory> getCategories() {
        return categories;
    }

    public void setCategories(List<TodoCategory> categories) {
        this.categories = categories;
    }

    public Integer getCurrentCategoryId() {
        return currentCategoryId;
    }

    public void setCurrentCategoryId(Integer currentCategoryId) {
        this.currentCategoryId = currentCategoryId;
        if (getCurrent() != null) {
            if (currentCategoryId == null) {
                getCurrent().setCategory(null);
            } else {
                for (TodoCategory category : getCategories()) {
                    if (category.getId() == currentCategoryId) {
                        getCurrent().setCategory(category);
                        break;
                    }
                }
            }
        }
    }

    public Integer getCurrentStatusId() {
        return currentStatusId;
    }

    public void setCurrentStatusId(Integer currentStatusId) {
        this.currentStatusId = currentStatusId;
        if (getCurrent() != null) {
            if (currentStatusId == null) {
                getCurrent().setStatus(null);
            } else {
                for (TodoStatus status : getStatuses()) {
                    if (status.getId() == currentStatusId) {
                        getCurrent().setStatus(status);
                        break;
                    }
                }
            }
        }
    }

    public List<SelectItem> getStatusItems() {
        return statusItems;
    }

    public void setStatusItems(List<SelectItem> statusItems) {
        this.statusItems = statusItems;
    }

    public List<SelectItem> getCategoryItems() {
        return categoryItems;
    }

    public void setCategoryItems(List<SelectItem> categories) {
        this.categoryItems = categories;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getListName() {
        return listName;
    }

    public void setListName(String listName) {
        this.listName = listName;
    }

    @Override
    public Todo getCurrent() {
        return super.getCurrent();
    }

    @Override
    public void setCurrent(Todo current) {
        super.setCurrent(current);
        TodoCategory c = current.getCategory();
        this.currentCategoryId = c == null ? null : c.getId();
        TodoStatus s = current.getStatus();
        this.currentStatusId = s == null ? null : s.getId();
    }

    public List<Todo> getTodo() {
        return todo;
    }

    public void setTodo(List<Todo> todo) {
        this.todo = todo;
        todoText=new ArrayList<>();
        if(todo!=null){
            for (Todo todo1 : todo) {
                todoText.add(new TodoText(todo1));
            }
        }
    }

    public List<TodoText> getTodoText() {
        return todoText;
    }

    public List<Todo> getInProgress() {
        return inProgress;
    }

    public void setInProgress(List<Todo> inProgress) {
        this.inProgress = inProgress;
    }

    public List<Todo> getToVerify() {
        return toVerify;
    }

    public void setToVerify(List<Todo> toVerify) {
        this.toVerify = toVerify;
    }

    public List<Todo> getDone() {
        return done;
    }

    public void setDone(List<Todo> done) {
        this.done = done;
    }

}
