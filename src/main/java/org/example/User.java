package org.example;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.experimental.FieldDefaults;

/**
 * @author 1ommy
 * @version 01.11.2023
 */

@AllArgsConstructor
@Builder
@Data
/*@NoArgsConstructor
@Getter
@Setter
@ToString
@EqualsAndHashCode*/
@FieldDefaults(makeFinal = true, level = AccessLevel.PACKAGE) // package-private
public class User {
    int age;
    int id;
    int weight;
    int height;
    int piskaSize;
    int siskaSize;
    String name;
    String city;

}
