package com.app.greenFuxes.entity.office;

import com.app.greenFuxes.entity.reservedDate.ReservedDate;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class Office {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private int capacity;

    @OneToMany(mappedBy = "office")
    private List<ReservedDate> reservedDates;
}
