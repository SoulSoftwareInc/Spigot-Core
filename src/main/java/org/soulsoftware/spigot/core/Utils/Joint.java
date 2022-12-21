package org.soulsoftware.spigot.core.Utils;

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
