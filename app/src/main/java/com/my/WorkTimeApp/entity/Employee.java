package com.my.WorkTimeApp.entity;

import java.util.Objects;

public class Employee {
    private Long id;
    private String name;
    public Long getId() {
            return id;
        }
    public void setId(Long id) {
            this.id = id;
        }
    public String getName() {
            return name;
        }
     public void setName(String name) {
            this.name = name;
        }

    public Employee(String name) {
        this.name = name;
    }

    public String forSave() {
        return "id:" + getId() + ", name: " + getName();
    }

    @Override
        public boolean equals(Object obj) {
            if (obj == this) {
                return true;
            }

            if (!(obj instanceof Employee)) {
                return false;
            }

            Employee other = (Employee) obj;
            return Objects.equals(id, other.id) && Objects.equals(name, other.name);
        }

    @Override
    public int hashCode() {
            return Objects.hash(id, name);
        }

    @Override
    public String toString() {
        return name;
    }
}
