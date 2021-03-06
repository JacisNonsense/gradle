/*
 * Copyright 2011 the original author or authors.
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
package org.gradle.api.internal.artifacts.ivyservice.ivyresolve

import org.gradle.api.artifacts.ComponentMetadataSupplier
import org.gradle.api.artifacts.component.ModuleComponentIdentifier
import org.gradle.api.internal.artifacts.ImmutableModuleIdentifierFactory
import org.gradle.api.internal.artifacts.ivyservice.ivyresolve.parser.DescriptorParseContext
import org.gradle.api.internal.artifacts.repositories.resolver.ExternalResourceResolver
import org.gradle.api.internal.artifacts.repositories.resolver.ResourcePattern
import org.gradle.api.internal.artifacts.repositories.resolver.VersionLister
import org.gradle.api.internal.component.ArtifactType
import org.gradle.caching.internal.BuildCacheHasher
import org.gradle.internal.component.external.model.ModuleComponentArtifactIdentifier
import org.gradle.internal.component.external.model.ModuleComponentArtifactMetadata
import org.gradle.internal.component.external.model.MutableModuleComponentResolveMetadata
import org.gradle.internal.component.model.IvyArtifactName
import org.gradle.internal.resource.ExternalResourceRepository
import org.gradle.internal.resource.local.FileResourceRepository
import org.gradle.internal.resource.local.FileStore
import org.gradle.internal.resource.local.LocallyAvailableExternalResource
import org.gradle.internal.resource.local.LocallyAvailableResourceFinder
import org.gradle.internal.resource.transfer.CacheAwareExternalResourceAccessor
import spock.lang.Specification

import java.lang.reflect.Field

public class DependencyResolverIdentifierTest extends Specification {
    private final static Field IVY = ExternalResourceResolver.getDeclaredField('ivyPatterns')
    private final static Field ARTIFACT = ExternalResourceResolver.getDeclaredField('artifactPatterns')

    def "dependency resolvers of type ExternalResourceResolver are differentiated by their patterns"() {
        given:
        ExternalResourceResolver resolver1 = resolver()
        ExternalResourceResolver resolver1a = resolver()
        ExternalResourceResolver resolver2 = resolver()
        ExternalResourceResolver resolver2a = resolver()

        patterns(resolver1, IVY, ['ivy1', 'ivy2'])
        patterns(resolver1, ARTIFACT, ['artifact1', 'artifact2'])
        patterns(resolver1a, IVY, ['ivy1', 'ivy2'])
        patterns(resolver1a, ARTIFACT, ['artifact1', 'artifact2'])
        patterns(resolver2, IVY, ['ivy1', 'different'])
        patterns(resolver2, ARTIFACT, ['artifact1', 'artifact2'])
        patterns(resolver2a, IVY, ['ivy1', 'ivy2'])
        patterns(resolver2a, ARTIFACT, ['artifact1', 'different'])

        expect:
        id(resolver1) == id(resolver1a)
        id(resolver1) != id(resolver2)
        id(resolver1) != id(resolver2a)
        id(resolver2) != id(resolver2a)
    }

    def patterns(ExternalResourceResolver resolver, Field field, List<String> patterns) {
        field.accessible = true
        field.set(resolver, patterns.collect { p -> Mock(ResourcePattern) {
            getPattern() >> p
        }})
    }

    def id(ExternalResourceResolver resolver) {
        resolver.getId()
    }

    def resolver() {
        return new TestResolver("repo", false, Stub(ExternalResourceRepository), Stub(CacheAwareExternalResourceAccessor), Stub(VersionLister), Stub(LocallyAvailableResourceFinder), Stub(FileStore), Stub(ImmutableModuleIdentifierFactory), Stub(FileResourceRepository))
    }

    static class TestResolver extends ExternalResourceResolver {
        TestResolver(String name, boolean local, ExternalResourceRepository repository, CacheAwareExternalResourceAccessor cachingResourceAccessor, VersionLister versionLister, LocallyAvailableResourceFinder<ModuleComponentArtifactMetadata> locallyAvailableResourceFinder, FileStore<ModuleComponentArtifactIdentifier> artifactFileStore, ImmutableModuleIdentifierFactory moduleIdentifierFactory, FileResourceRepository fileResourceRepository) {
            super(name, local, repository, cachingResourceAccessor, versionLister, locallyAvailableResourceFinder, artifactFileStore, moduleIdentifierFactory, fileResourceRepository)
        }

        @Override
        protected void appendId(BuildCacheHasher hasher) {
        }

        @Override
        protected Class getSupportedMetadataType() {
            throw new UnsupportedOperationException()
        }

        @Override
        protected MutableModuleComponentResolveMetadata createDefaultComponentResolveMetaData(ModuleComponentIdentifier moduleComponentIdentifier, Set artifacts) {
            throw new UnsupportedOperationException()
        }

        @Override
        protected MutableModuleComponentResolveMetadata parseMetaDataFromResource(ModuleComponentIdentifier moduleComponentIdentifier, LocallyAvailableExternalResource cachedResource, DescriptorParseContext context) {
            throw new UnsupportedOperationException()
        }

        @Override
        protected boolean isMetaDataArtifact(ArtifactType artifactType) {
            throw new UnsupportedOperationException()
        }

        @Override
        protected IvyArtifactName getMetaDataArtifactName(String moduleName) {
            throw new UnsupportedOperationException()
        }

        @Override
        ModuleComponentRepositoryAccess getLocalAccess() {
            throw new UnsupportedOperationException()
        }

        @Override
        ModuleComponentRepositoryAccess getRemoteAccess() {
            throw new UnsupportedOperationException()
        }

        @Override
        ComponentMetadataSupplier createMetadataSupplier() {
            throw new UnsupportedOperationException()
        }
    }
}
