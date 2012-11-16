/*******************************************************************************
 * Copyright (c) 2012 EclipseSource and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    EclipseSource - initial API and implementation
 ******************************************************************************/
package com.eclipsesource.tabris.internal.geolocation;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;

import org.junit.Before;
import org.junit.Test;

import com.eclipsesource.tabris.geolocation.GeolocationCallback;
import com.eclipsesource.tabris.geolocation.GeolocationOptions;
import com.eclipsesource.tabris.geolocation.Position;
import com.eclipsesource.tabris.geolocation.PositionError;
import com.eclipsesource.tabris.internal.geolocation.GeolocationAdapter;
import com.eclipsesource.tabris.internal.geolocation.GeolocationAdapter.NeedsPositionFlavor;


public class GeolocationAdapterTest {
  
  private GeolocationAdapter adapter;

  @Before
  public void setUp() {
    adapter = new GeolocationAdapter();
  }
  
  @Test
  public void testPosition() {
   Position position = mock( Position.class );
    
   adapter.setPosition( position );
   
   assertEquals( position, adapter.getPosition() );
  }
  
  @Test
  public void testCallback() {
    GeolocationCallback mock = mock( GeolocationCallback.class );
    
    adapter.setCallback( mock );
    
    assertEquals( mock, adapter.getCallback() );
  }
  
  @Test
  public void testError() {
    PositionError mock = mock( PositionError.class );
    
    adapter.setError( mock );
    
    assertEquals( mock, adapter.getError() );
  }
  
  @Test
  public void testOptions() {
    GeolocationOptions mock = mock( GeolocationOptions.class );
    
    adapter.setOptions( mock );
    
    assertEquals( mock, adapter.getOptions() );
  }
  
  @Test
  public void testFlavor() {
    adapter.setFlavor( NeedsPositionFlavor.CONTINUOUS );
    
    assertEquals( NeedsPositionFlavor.CONTINUOUS, adapter.getFlavor() );
  }
  
  @Test
  public void testDispose() {
    adapter.dispose();
    
    assertTrue( adapter.isDisposed() );
  }
  
  @Test
  public void testDestroy() {
    adapter.destroy();
    
    assertTrue( adapter.isDestroyed() );
  }
}