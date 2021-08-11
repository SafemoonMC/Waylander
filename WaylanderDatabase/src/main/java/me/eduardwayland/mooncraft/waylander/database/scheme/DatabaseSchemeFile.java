package me.eduardwayland.mooncraft.waylander.database.scheme;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;

import java.io.File;
import java.io.IOException;

@Getter
@EqualsAndHashCode
@AllArgsConstructor
public abstract class DatabaseSchemeFile {
    
    /*
    Fields
     */
    private final File file;
    
    /*
    Abstract Methods
     */
    public abstract DatabaseScheme parse() throws IOException;
}