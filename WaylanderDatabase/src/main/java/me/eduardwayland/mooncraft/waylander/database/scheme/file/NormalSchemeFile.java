package me.eduardwayland.mooncraft.waylander.database.scheme.file;

import lombok.Getter;
import me.eduardwayland.mooncraft.waylander.database.queries.Query;
import me.eduardwayland.mooncraft.waylander.database.scheme.DatabaseSchemeFile;
import me.eduardwayland.mooncraft.waylander.database.scheme.db.NormalDatabaseScheme;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.LinkedList;
import java.util.List;
import java.util.regex.Pattern;

@Getter
public final class NormalSchemeFile extends DatabaseSchemeFile {
    
    /*
    Constants
     */
    private static final Pattern COMMENT_PATTERN = Pattern.compile("((#+.+)|#.*?[\\r\\n])[\\r\\n]*");
    
    /*
    Constructor
     */
    public NormalSchemeFile(File file) {
        super(file);
    }
    
    /*
    Override Methods
     */
    @Override
    public NormalDatabaseScheme parse() throws IOException {
        List<String> lines = readFileLines();
        
        LinkedList<Query> queryList = readQueryList(lines);
        
        return new NormalDatabaseScheme(getFile().getName(), queryList);
    }
    
    /*
    Methods
     */
    private List<String> readFileLines() throws IOException {
        List<String> lines = Files.readAllLines(getFile().toPath());
        for (int i = 0; i < lines.size(); i++) {
            String line = lines.get(i).trim();
            String changedLine = COMMENT_PATTERN.matcher(line).replaceAll("");
            if (changedLine.isEmpty()) {
                lines.remove(i);
                i--;
            } else lines.set(i, changedLine);
        }
        return lines;
    }
    
    private LinkedList<Query> readQueryList(List<String> lines) {
        LinkedList<Query> queries = new LinkedList<>();
        
        StringBuilder queryStringBuilder = new StringBuilder();
        for (String line : lines) {
            queryStringBuilder.append(line);
            if (!line.endsWith(";")) continue;
            queries.add(Query.single(queryStringBuilder.toString()).build());
            
            queryStringBuilder = new StringBuilder();
        }
        
        return queries;
    }
}