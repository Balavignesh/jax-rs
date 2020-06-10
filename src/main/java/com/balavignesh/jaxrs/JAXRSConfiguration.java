package com.balavignesh.jaxrs;

import com.balavignesh.jaxrs.services.CustomerResource;
import java.util.HashSet;
import java.util.Set;
import javax.ws.rs.ApplicationPath;
import javax.ws.rs.core.Application;

/**
 * Configures JAX-RS for the application.
 * @author Juneau
 */
@ApplicationPath("services")
public class JAXRSConfiguration extends Application {
    
    private Set<Object> singletons = new HashSet<Object>();
   private Set<Class<?>> empty = new HashSet<Class<?>>();

   public JAXRSConfiguration() {
      singletons.add(new CustomerResource());
   }

   @Override
   public Set<Class<?>> getClasses() {
      return empty;
   }

   @Override
   public Set<Object> getSingletons() {
      return singletons;
   }
    
}
