package org.soulsoftware.spigot.core.Utilities;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class Joint<T> {
    private T key;
    private T value;
}
