# Frontend Resources

This plugin allows to execute commands on module deployment with the 
purpose of acquiring information about front end resources required by 
the module.

## Use

To use add plugin to your pom.xml
```
<plugin>
    <groupId>org.jahia.plugins</groupId>
    <artifactId>frontend-resources-maven-plugin</artifactId>
    <version>1.0-SNAPSHOT</version>
    <configuration>
        <workingDirectory>src/main/frontend</workingDirectory>
        <outputDirectory>src/main/frontend</outputDirectory>
        <command>npm ls -json=true -prod=true -depth=0</command>
        <additionalCommands>
            <commandone>ls -a</commandone> //Example only
        </additionalCommands>
    </configuration>
    <executions>
        <execution>
            <phase>generate-resources</phase>
            <goals>
                <goal>frontend-resources</goal>
            </goals>
        </execution>
    </executions>
</plugin>
```
