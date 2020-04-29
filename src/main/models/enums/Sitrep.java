package models.enums;


/**
 * Represents the different situations a team can be in during a Run at storm the wall
 * Purpose: be able to classify Run(s) by different situations
 *
 * DNA -> Do Not Advance
 * DNS -> Did Not Show
 * DNF -> Did Not Finish
 * DQ -> Disqualified
 * None -> No special situation or status
 *
 * <p>FT staff may ask for different types of DQ, ex. DQ1, DQ2, DQ3, etc. each DQ would refer to a different
 * disqualification reason.</p>
 *
 */
public enum Sitrep {
    DNA("DNA"),
    DNS("DNS"),
    DNF("DNF"),
    DQ("DQ"),
    NONE("None");

    Sitrep(String value) {

    }
}