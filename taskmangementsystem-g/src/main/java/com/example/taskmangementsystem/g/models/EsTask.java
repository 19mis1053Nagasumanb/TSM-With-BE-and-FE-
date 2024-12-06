package com.example.taskmangementsystem.g.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.elasticsearch.annotations.Document;

import java.time.Instant;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Document(indexName = "optimizedes")
@JsonIgnoreProperties(ignoreUnknown = true)
public class EsTask {



    @Id
    private String id ;// Generates ID based on current time
    private String username;
    private String task;
    @Enumerated(EnumType.STRING)
    private TaskPriority priority;

    private String logHours ;


    private String day;

    @Enumerated(EnumType.STRING)
    private TaskStatus status;


    private Long date;

    @JsonProperty("date")
    public String getFormattedDate() {
        if (this.date != null) {
            return Instant.ofEpochMilli(this.date)
                    .atZone(ZoneId.systemDefault())
                    .toLocalDate()
                    .toString(); // Converts to yyyy-MM-dd format
        }
        return null; // Return null if date is null
    }

    @JsonProperty("date")
    public void setFormattedDate(Long date) {
        this.date = date;
    }
//


    private Long time; // Stores the time in milliseconds since epoch


@JsonProperty("time")
public String getFormattedTime() {
    // Convert the stored milliseconds into a LocalTime formatted as HH:mm:ss
    long effectiveTime = (this.time != null) ? this.time : System.currentTimeMillis();
    return Instant.ofEpochMilli(effectiveTime)
            .atZone(ZoneOffset.UTC)
            .toLocalTime()
            .format(DateTimeFormatter.ofPattern("HH:mm:ss"));
}

    @JsonProperty("time")
    public void setFormattedTime(Long time) {
        this.time = time;
    }

//    @ManyToOne
//    @JoinColumn(name = "user_id")
//    private App_User appUser;


}


