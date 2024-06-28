package net.whydah.uss.entity;

import java.io.Serializable;
import java.time.LocalDateTime;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

@Entity
@Data
@Table(name="OLD_USER")
public class OldUserEntity implements Serializable  {

	@Id
	protected String id;
    protected String username;
    protected String firstName;
    protected String lastName;
    protected String personRef;
    protected String email;
    protected String cellPhone;
	protected LocalDateTime lastLoginTime;
	protected boolean notified;
}
