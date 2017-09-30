/*
 * Copyright 2016 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.gradle.internal.component.external.model;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import org.gradle.api.artifacts.ModuleVersionIdentifier;
import org.gradle.api.artifacts.component.ModuleComponentIdentifier;
import org.gradle.internal.component.external.descriptor.Artifact;
import org.gradle.internal.component.external.descriptor.Configuration;
import org.gradle.internal.component.external.descriptor.ModuleDescriptorState;
import org.gradle.internal.component.external.descriptor.MutableModuleDescriptorState;
import org.gradle.internal.component.model.DefaultIvyArtifactName;
import org.gradle.internal.component.model.DependencyMetadata;

import java.util.Collection;
import java.util.Map;

import static org.gradle.api.artifacts.Dependency.DEFAULT_CONFIGURATION;

public class DefaultMutableIvyModuleResolveMetadata extends AbstractMutableModuleComponentResolveMetadata implements MutableIvyModuleResolveMetadata {
    private final ImmutableList<Artifact> artifacts;

    /**
     * Creates default metadata for an Ivy module version with no ivy.xml descriptor.
     */
    public DefaultMutableIvyModuleResolveMetadata(ModuleVersionIdentifier id, ModuleComponentIdentifier componentIdentifier) {
        this(id, componentIdentifier,
            MutableModuleDescriptorState.createModuleDescriptor(componentIdentifier),
            ImmutableList.of(new Configuration(DEFAULT_CONFIGURATION, true, true, ImmutableSet.<String>of())),
            ImmutableList.<DependencyMetadata>of(),
            ImmutableList.of(new Artifact(new DefaultIvyArtifactName(componentIdentifier.getModule(), "jar", "jar"), ImmutableSet.of(DEFAULT_CONFIGURATION))));
    }

    public DefaultMutableIvyModuleResolveMetadata(ModuleVersionIdentifier id, ModuleComponentIdentifier componentIdentifier, ModuleDescriptorState descriptor, Collection<Configuration> configurations, Collection<? extends DependencyMetadata> dependencies, Collection<? extends Artifact> artifacts) {
        super(id, componentIdentifier, descriptor, toMap(configurations), ImmutableList.copyOf(dependencies));
        this.artifacts = ImmutableList.copyOf(artifacts);
    }

    public DefaultMutableIvyModuleResolveMetadata(IvyModuleResolveMetadata metadata) {
        super(metadata);
        this.artifacts = metadata.getArtifactDefinitions();
    }

    private static Map<String, Configuration> toMap(Collection<Configuration> configurations) {
        ImmutableMap.Builder<String, Configuration> builder = ImmutableMap.builder();
        for (Configuration configuration : configurations) {
            builder.put(configuration.getName(), configuration);
        }
        return builder.build();
    }

    @Override
    public ImmutableList<Artifact> getArtifactDefinitions() {
        return artifacts;
    }

    @Override
    public String getBranch() {
        return getDescriptor().getBranch();
    }

    @Override
    public IvyModuleResolveMetadata asImmutable() {
        return new DefaultIvyModuleResolveMetadata(this);
    }
}
