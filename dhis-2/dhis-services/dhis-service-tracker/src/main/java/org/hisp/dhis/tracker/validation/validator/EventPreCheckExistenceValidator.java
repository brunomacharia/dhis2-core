/*
 * Copyright (c) 2004-2022, University of Oslo
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * Redistributions of source code must retain the above copyright notice, this
 * list of conditions and the following disclaimer.
 *
 * Redistributions in binary form must reproduce the above copyright notice,
 * this list of conditions and the following disclaimer in the documentation
 * and/or other materials provided with the distribution.
 * Neither the name of the HISP project nor the names of its contributors may
 * be used to endorse or promote products derived from this software without
 * specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR
 * ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON
 * ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package org.hisp.dhis.tracker.validation.validator;

import static org.hisp.dhis.tracker.validation.ValidationCode.E1030;
import static org.hisp.dhis.tracker.validation.ValidationCode.E1032;
import static org.hisp.dhis.tracker.validation.ValidationCode.E1082;

import org.hisp.dhis.program.ProgramStageInstance;
import org.hisp.dhis.tracker.TrackerImportStrategy;
import org.hisp.dhis.tracker.bundle.TrackerBundle;
import org.hisp.dhis.tracker.domain.Event;
import org.hisp.dhis.tracker.validation.Reporter;
import org.hisp.dhis.tracker.validation.Validator;
import org.springframework.stereotype.Component;

/**
 * @author Morten Svanæs <msvanaes@dhis2.org>
 */
@Component
public class EventPreCheckExistenceValidator
    implements Validator<Event>
{
    @Override
    public void validate( Reporter reporter, TrackerBundle bundle, Event event )
    {
        TrackerImportStrategy importStrategy = bundle.getStrategy( event );

        ProgramStageInstance existingPsi = bundle.getPreheat().getEvent( event.getEvent() );

        // If the event is soft-deleted no operation is allowed
        if ( existingPsi != null && existingPsi.isDeleted() )
        {
            reporter.addError( event, E1082, event.getEvent() );
            return;
        }

        if ( existingPsi != null && importStrategy.isCreate() )
        {
            reporter.addError( event, E1030, event.getEvent() );
        }
        else if ( existingPsi == null && importStrategy.isUpdateOrDelete() )
        {
            reporter.addError( event, E1032, event.getEvent() );
        }
    }

    @Override
    public boolean needsToRun( TrackerImportStrategy strategy )
    {
        return true;
    }

}