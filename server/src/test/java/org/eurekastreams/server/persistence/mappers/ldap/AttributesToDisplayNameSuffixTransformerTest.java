/*
 * Copyright (c) 2013 Lockheed Martin Corporation
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
package org.eurekastreams.server.persistence.mappers.ldap;

import org.junit.Test;
import javax.naming.directory.Attributes;
import javax.naming.directory.BasicAttributes;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

/**
 * Test fixture for AttributesToDisplayNameSuffixTransformer.
 */
public class AttributesToDisplayNameSuffixTransformerTest
{
    /**
     * Test transform() with a match.
     */
    @Test
    public void testTransformWithMatch()
    {
        AttributesToDisplayNameSuffixTransformer sut;
        sut = new AttributesToDisplayNameSuffixTransformer("foo", "^.+Esquire, D.F.A.$", " (LOL)");

        Attributes attributes = new BasicAttributes("foo", "Dr. Stephen Colbert Esquire, D.F.A.");
        String result = sut.transform(attributes);
        assertEquals(" (LOL)", result);
    }

    /**
     * Test transform() with no match.
     */
    @Test
    public void testTransformWithNoMatch()
    {
        AttributesToDisplayNameSuffixTransformer sut;
        sut = new AttributesToDisplayNameSuffixTransformer("foo", "^.+Esquire, D.F.A.$", " (LOL)");

        Attributes attributes = new BasicAttributes("foo", "Dr. Stephen Colbert Esquire, M.D.");
        String result = sut.transform(attributes);
        assertNull(result);
    }

    /**
     * Test transform() with the target attribute missing.
     */
    @Test
    public void testTransformWithMissingAttribute()
    {
        AttributesToDisplayNameSuffixTransformer sut;
        sut = new AttributesToDisplayNameSuffixTransformer("foo", "^.+Esquire, D.F.A.$", " (LOL)");

        Attributes attributes = new BasicAttributes("bar", "Dr. Stephen Colbert Esquire, D.F.A.");
        String result = sut.transform(attributes);
        assertNull(result);
    }
}
