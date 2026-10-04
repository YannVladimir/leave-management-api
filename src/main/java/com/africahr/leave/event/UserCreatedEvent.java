package com.africahr.leave.event;

import com.africahr.leave.model.User;
import lombok.Getter;
import org.springframework.context.ApplicationEvent;

@Getter
public class UserCreatedEvent extends ApplicationEvent {
    private final User user;

    public UserCreatedEvent(Object source, User user) {
        super(source);
        this.user = user;
    }
} 