package com.ftn.uns.scraper.service.filter;

import com.ftn.uns.scraper.query.model.Filters;
import com.ftn.uns.scraper.site.SiteType;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.error.YAMLException;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;

public class FilterFactory {

    public static Filters getFilters(SiteType site) throws FileNotFoundException {
        try {
            FileReader reader = new FileReader(new File("src/main/resources/filters." + site.name().toLowerCase() + ".yaml"));
            Yaml yaml = new Yaml();
            return yaml.loadAs(reader, Filters.class);
        }catch (YAMLException e){
            throw new FileNotFoundException();
        }
    }
}
