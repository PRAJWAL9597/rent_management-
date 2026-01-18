package com.client.rentmanagement.tenant;

import com.client.rentmanagement.common.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;
import java.time.LocalDate;

@Getter
@Setter
@Entity
@Table(name = "tenant")
public class Tenant extends BaseEntity { // Inherits ID, Active, and Auto-Timestamps

    @Column(nullable = false, length = 100)
    private String name; //

    @Column(name = "room_no", nullable = false, length = 20, unique = true)
    private String roomNo; // Owner uses this as the UID

    @Column(name = "phone_no", nullable = false, length = 15)
    private String phoneNo; //

    @Column(name = "aadhar_no", length = 12, unique = true)
    private String aadharNo; // Client requirement for 12-digit ID

    @Column(name = "joining_date", nullable = false)
    private LocalDate joiningDate; // Used to anchor the very first reading

    @Column(name = "meter_id", nullable = false, length = 50)
    private String meterId; //

    @Column(length = 100)
    private String email; //
}