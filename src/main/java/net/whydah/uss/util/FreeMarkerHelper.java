package net.whydah.uss.util;

import java.io.File;
import java.io.IOException;
import java.io.StringWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.sql.Date;
import java.sql.Time;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import freemarker.cache.ClassTemplateLoader;
import freemarker.cache.FileTemplateLoader;
import freemarker.cache.MultiTemplateLoader;
import freemarker.cache.TemplateLoader;
import freemarker.template.Configuration;
import freemarker.template.DefaultObjectWrapper;
import freemarker.template.SimpleDate;
import freemarker.template.Template;
import freemarker.template.TemplateModel;
import freemarker.template.TemplateModelException;
import no.api.freemarker.java8.Java8ObjectWrapper;

public class FreeMarkerHelper {

	private static final Logger log = LoggerFactory.getLogger(FreeMarkerHelper.class);

	private static Configuration freemarkerConfig;

	public static final Path path = Paths.get("./forcelog.txt");
	static {
		loadTemplates();
	}

	public static String createBody(String templateName, Map<String, Object> model) {
		StringWriter stringWriter = new StringWriter();
		try {
			Template template = freemarkerConfig.getTemplate(templateName);
			template.process(model, stringWriter);
		} catch (RuntimeException e) {
			throw e;
		} catch (Exception e) {
			log.error("Exception in rendering ftp body", e);
			log.error("Model", convertWithStream(model));

			try {
				String s = "Exception in rendering ftp body: " + Arrays.toString(e.getStackTrace());
				Files.write(path, s.getBytes(StandardCharsets.UTF_8),
						StandardOpenOption.CREATE,
						StandardOpenOption.APPEND);
				String s2 = "Model: " + convertWithStream(model);
				Files.write(path, s2.getBytes(StandardCharsets.UTF_8),
						StandardOpenOption.CREATE,
						StandardOpenOption.APPEND);

			} catch (Exception eee) {

			}
			String s = "Exception in rendering ftp body: " + Arrays.toString(e.getStackTrace());
			String s2 = "Model: " + convertWithStream(model);
			throw new RuntimeException("Populating template failed. templateName=" + templateName + "\n\ns:" + s + "\n\nm:" + s2, e);
		}
		return stringWriter.toString();
	}

	public static String convertWithStream(Map<String, ?> map) {
		String mapAsString = map.keySet().stream()
				.map(key -> key + "=" + map.get(key))
				.collect(Collectors.joining(", ", "{", "}"));
		return mapAsString;
	}
	
	private static class CustomObjectWrapper extends DefaultObjectWrapper {
	    @Override
	    public TemplateModel wrap(Object obj) throws TemplateModelException {
	        if (obj instanceof LocalDateTime) {
	            Timestamp timestamp = Timestamp.valueOf((LocalDateTime) obj);
	            return new SimpleDate(timestamp);
	        }
	        if (obj instanceof LocalDate) {
	            Date date = Date.valueOf((LocalDate) obj);
	            return new SimpleDate(date);
	        }
	        if (obj instanceof LocalTime) {
	            Time time = Time.valueOf((LocalTime) obj);
	            return new SimpleDate(time);
	        }
	        return super.wrap(obj);
	    }
	}

	

	private static void loadTemplates() {
		try {
			freemarkerConfig = new Configuration(Configuration.VERSION_2_3_31);
			
			
			File customTemplate = new File("./templates");
			FileTemplateLoader ftl = null;
			if (customTemplate.exists()) {
				ftl = new FileTemplateLoader(customTemplate);
			}
			ClassTemplateLoader ctl = new ClassTemplateLoader(FreeMarkerHelper.class, "/templates");

			TemplateLoader[] loaders = null;
			if (ftl != null) {
				loaders = new TemplateLoader[]{ftl, ctl};
			} else {
				loaders = new TemplateLoader[]{ctl};
			}


			MultiTemplateLoader mtl = new MultiTemplateLoader(loaders);
			
			freemarkerConfig.setObjectWrapper(new Java8ObjectWrapper(freemarkerConfig.getIncompatibleImprovements()));
			freemarkerConfig.setTemplateLoader(mtl);
			freemarkerConfig.setDefaultEncoding("UTF-8");
			freemarkerConfig.setLocalizedLookup(false);
			freemarkerConfig.setTemplateUpdateDelayMilliseconds(6000);
			
			 
		} catch (IOException ioe) {
			log.error("Unable to load/process freemarker tenmplates", ioe);
		}
	}


}
