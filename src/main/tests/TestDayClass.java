package tests;

import models.Day;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.util.Calendar;


public class TestDayClass {

    Day day;

    @BeforeEach
    public void BeforeEach() {
        day = new Day(Calendar.getInstance(), 1);
    }



}
