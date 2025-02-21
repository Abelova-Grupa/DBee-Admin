package com.abelovagrupa.dbeeadmin.model.trigger;

import com.abelovagrupa.dbeeadmin.model.table.Table;

import java.time.LocalDateTime;
import java.util.Objects;

public class Trigger {
    private String name;
    private Event event;
    private Timing timing;
    private Table table;
    private String triggerDescription;
    private LocalDateTime createdAt;
    private boolean isEnabled;
    private String definer;
    private String statement;

    public Trigger() {
    }

    public Trigger(String name, Event event, Timing timing, Table table, String triggerDescription, LocalDateTime createdAt, boolean isEnabled, String definer, String statement) {
        this.name = name;
        this.event = event;
        this.timing = timing;
        this.table = table;
        this.triggerDescription = triggerDescription;
        this.createdAt = createdAt;
        this.isEnabled = isEnabled;
        this.definer = definer;
        this.statement = statement;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Event getEvent() {
        return event;
    }

    public void setEvent(Event event) {
        this.event = event;
    }

    public Timing getTiming() {
        return timing;
    }

    public void setTiming(Timing timing) {
        this.timing = timing;
    }

    public Table getTable() {
        return table;
    }

    public void setTable(Table table) {
        this.table = table;
    }

    public String getTriggerDescription() {
        return triggerDescription;
    }

    public void setTriggerDescription(String triggerDescription) {
        this.triggerDescription = triggerDescription;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public boolean isEnabled() {
        return isEnabled;
    }

    public void setEnabled(boolean enabled) {
        isEnabled = enabled;
    }

    public String getDefiner() {
        return definer;
    }

    public void setDefiner(String definer) {
        this.definer = definer;
    }

    public String getStatement() {
        return statement;
    }

    public void setStatement(String statement) {
        this.statement = statement;
    }

    @Override
    public String toString() {
        return "Trigger{" +
                "name='" + name + '\'' +
                ", event=" + event +
                ", timing=" + timing +
                ", table=" + table +
                ", triggerDescription='" + triggerDescription + '\'' +
                ", createdAt=" + createdAt +
                ", isEnabled=" + isEnabled +
                ", definer='" + definer + '\'' +
                ", statement='" + statement + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof Trigger trigger)) return false;
        return isEnabled == trigger.isEnabled && Objects.equals(name, trigger.name) && event == trigger.event && timing == trigger.timing && Objects.equals(table, trigger.table) && Objects.equals(triggerDescription, trigger.triggerDescription) && Objects.equals(createdAt, trigger.createdAt) && Objects.equals(definer, trigger.definer) && Objects.equals(statement, trigger.statement);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, event, timing, table, triggerDescription, createdAt, isEnabled, definer, statement);
    }
}