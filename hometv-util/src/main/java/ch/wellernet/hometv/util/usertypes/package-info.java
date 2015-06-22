@TypeDefs({ @TypeDef(name = "file", defaultForType = File.class, typeClass = FileType.class),
        @TypeDef(name = "duration", defaultForType = Duration.class, typeClass = DurationType.class) })
package ch.wellernet.hometv.util.usertypes;

import java.io.File;

import org.hibernate.annotations.TypeDef;
import org.hibernate.annotations.TypeDefs;
import org.joda.time.Duration;

