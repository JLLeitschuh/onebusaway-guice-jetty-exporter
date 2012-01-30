/**
 * Copyright (C) 2011 Brian Ferris <bdferris@onebusaway.org>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.onebusaway.guice.jetty_exporter;

import java.util.ArrayList;
import java.util.List;

import com.google.inject.AbstractModule;
import com.google.inject.TypeLiteral;
import com.google.inject.matcher.Matchers;
import com.google.inject.spi.InjectionListener;
import com.google.inject.spi.TypeEncounter;
import com.google.inject.spi.TypeListener;

public class JettyExporterModule extends AbstractModule {

  private final List<ServletSource> _sources = new ArrayList<ServletSource>();
  
  protected List<ServletSource> getSources() {
    return _sources;
  }

  @Override
  protected void configure() {

    bindListener(Matchers.any(), new TypeListener() {
      @Override
      public <I> void hear(TypeLiteral<I> injectableType,
          TypeEncounter<I> encounter) {

        Class<? super I> type = injectableType.getRawType();

        if (ServletSource.class.isAssignableFrom(type)) {
          encounter.register(new InjectionListenerImpl<I>(_sources));
        }
      }
    });

    bind(JettyExporterServiceImpl.class).toInstance(
        new JettyExporterServiceImpl(_sources));
  }

  private static class InjectionListenerImpl<I> implements InjectionListener<I> {

    private final List<ServletSource> _sources;

    public InjectionListenerImpl(List<ServletSource> sources) {
      _sources = sources;
    }

    @Override
    public void afterInjection(I injectee) {
      _sources.add((ServletSource) injectee);
    }
  }
}
