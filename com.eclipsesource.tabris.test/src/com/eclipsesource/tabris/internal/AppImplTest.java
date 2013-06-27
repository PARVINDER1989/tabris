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
package com.eclipsesource.tabris.internal;

import static com.eclipsesource.tabris.app.EventType.PAUSE;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import java.io.Serializable;

import org.eclipse.rap.json.JsonObject;
import org.eclipse.rap.json.JsonValue;
import org.eclipse.rap.rwt.lifecycle.PhaseId;
import org.eclipse.rap.rwt.remote.RemoteObject;
import org.eclipse.rap.rwt.testfixture.Fixture;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InOrder;

import com.eclipsesource.tabris.app.App;
import com.eclipsesource.tabris.app.AppEvent;
import com.eclipsesource.tabris.app.AppListener;
import com.eclipsesource.tabris.app.BackNavigationListener;
import com.eclipsesource.tabris.test.TabrisTestUtil;


public class AppImplTest {

  @Before
  public void setUp() {
    Fixture.setUp();
    Fixture.fakePhase( PhaseId.PROCESS_ACTION );
  }

  @After
  public void tearDown() {
    Fixture.tearDown();
  }

  @Test
  public void testIsSerializable() {
    assertTrue( Serializable.class.isAssignableFrom( AppImpl.class ) );
  }

  @Test
  public void testAppListenerIsSerializable() {
    assertTrue( Serializable.class.isAssignableFrom( AppListener.class ) );
  }

  @Test
  public void testBackNavigationListenerIsSerializable() {
    assertTrue( Serializable.class.isAssignableFrom( BackNavigationListener.class ) );
  }

  @Test
  public void testAddListensTransportsListenOperation() {
    RemoteObject remoteObject = TabrisTestUtil.mockServiceObject();
    App app = new AppImpl();
    AppListener listener = mock( AppListener.class );

    app.addEventListener( PAUSE, listener );

    verify( remoteObject ).listen( "Pause", true );
  }

  @Test
  public void testAddEventListenersTransportsListenOperationOnce() {
    RemoteObject remoteObject = TabrisTestUtil.mockServiceObject();
    App app = new AppImpl();
    AppListener listener = mock( AppListener.class );
    AppListener listener2 = mock( AppListener.class );

    app.addEventListener( PAUSE, listener );
    app.addEventListener( PAUSE, listener2 );

    verify( remoteObject, times( 1 ) ).listen( "Pause", true );
  }

  @Test
  public void testRemoveListensTransportsListenOperation() {
    RemoteObject remoteObject = TabrisTestUtil.mockServiceObject();
    App app = new AppImpl();
    AppListener listener = mock( AppListener.class );
    app.addEventListener( PAUSE, listener );

    app.removeEventListener( PAUSE, listener );

    InOrder order = inOrder( remoteObject );
    order.verify( remoteObject ).listen( "Pause", true );
    order.verify( remoteObject ).listen( "Pause", false );
  }

  @Test
  public void testRemoveEventListenersTransportsListenOperationOnce() {
    RemoteObject remoteObject = TabrisTestUtil.mockServiceObject();
    App app = new AppImpl();
    AppListener listener = mock( AppListener.class );
    AppListener listener2 = mock( AppListener.class );
    app.addEventListener( PAUSE, listener );
    app.addEventListener( PAUSE, listener2 );

    app.removeEventListener( PAUSE, listener );
    app.removeEventListener( PAUSE, listener2 );

    InOrder order = inOrder( remoteObject );
    order.verify( remoteObject, times( 1 ) ).listen( "Pause", true );
    order.verify( remoteObject, times( 1 ) ).listen( "Pause", false );
  }

  @Test
  public void testRemoveNonExistingEventListenersDoesNotFail() {
    App app = new AppImpl();
    AppListener listener = mock( AppListener.class );

    app.removeEventListener( PAUSE, listener );
  }

  @Test
  public void testRemoveOneListenerDoesNotTransportsListenOperation() {
    RemoteObject remoteObject = TabrisTestUtil.mockServiceObject();
    App app = new AppImpl();
    AppListener listener = mock( AppListener.class );
    AppListener listener2 = mock( AppListener.class );
    app.addEventListener( PAUSE, listener );
    app.addEventListener( PAUSE, listener2 );

    app.removeEventListener( PAUSE, listener );

    InOrder order = inOrder( remoteObject );
    order.verify( remoteObject, times( 1 ) ).listen( "Pause", true );
    order.verify( remoteObject, never() ).listen( "Pause", false );
  }

  @Test
  public void testDelegatesNotify() {
    AppImpl app = new AppImpl();
    AppListener listener = mock( AppListener.class );
    app.addEventListener( PAUSE, listener );

    TabrisTestUtil.dispatchNotify( app.getRemoteObject(), "Pause", null );

    ArgumentCaptor<AppEvent> captor = ArgumentCaptor.forClass( AppEvent.class );
    verify( listener ).handleEvent( captor.capture() );
    assertSame( PAUSE, captor.getValue().getType() );
  }

  @Test
  public void testDelegatesNotifyToAllListeners() {
    AppImpl app = new AppImpl();
    AppListener listener = mock( AppListener.class );
    AppListener listener2 = mock( AppListener.class );
    app.addEventListener( PAUSE, listener );
    app.addEventListener( PAUSE, listener2 );

    TabrisTestUtil.dispatchNotify( app.getRemoteObject(), "Pause", null );

    ArgumentCaptor<AppEvent> captor = ArgumentCaptor.forClass( AppEvent.class );
    verify( listener ).handleEvent( captor.capture() );
    verify( listener2 ).handleEvent( captor.capture() );
    assertSame( PAUSE, captor.getAllValues().get( 0 ).getType() );
    assertSame( PAUSE, captor.getAllValues().get( 1 ).getType() );
  }

  @Test
  public void testDelegatesNotifyWithProperties() {
    AppImpl app = new AppImpl();
    AppListener listener = mock( AppListener.class );
    app.addEventListener( PAUSE, listener );
    JsonObject properties = new JsonObject();
    properties.add( "foo", "bar" );

    TabrisTestUtil.dispatchNotify( app.getRemoteObject(), "Pause", properties );

    ArgumentCaptor<AppEvent> captor = ArgumentCaptor.forClass( AppEvent.class );
    verify( listener ).handleEvent( captor.capture() );
    assertSame( PAUSE, captor.getValue().getType() );
    assertEquals( JsonValue.valueOf( "bar" ), captor.getValue().getProperty( "foo" ) );
  }

  @Test
  public void testAddBackNavigationListensTransportsListenOperation() {
    RemoteObject remoteObject = TabrisTestUtil.mockServiceObject();
    App app = new AppImpl();
    BackNavigationListener listener = mock( BackNavigationListener.class );

    app.addBackNavigationListener( listener );

    verify( remoteObject ).listen( "BackNavigation", true );
  }

  @Test
  public void testAddBackNavigationListenersTransportsListenOperationOnce() {
    RemoteObject remoteObject = TabrisTestUtil.mockServiceObject();
    App app = new AppImpl();
    BackNavigationListener listener = mock( BackNavigationListener.class );
    BackNavigationListener listener2 = mock( BackNavigationListener.class );

    app.addBackNavigationListener( listener );
    app.addBackNavigationListener( listener2 );

    verify( remoteObject, times( 1 ) ).listen( "BackNavigation", true );
  }

  @Test
  public void testRemoveBackNavigationListensTransportsListenOperation() {
    RemoteObject remoteObject = TabrisTestUtil.mockServiceObject();
    App app = new AppImpl();
    BackNavigationListener listener = mock( BackNavigationListener.class );
    app.addBackNavigationListener( listener );

    app.removeBackNavigationListener( listener );

    InOrder order = inOrder( remoteObject );
    order.verify( remoteObject ).listen( "BackNavigation", true );
    order.verify( remoteObject ).listen( "BackNavigation", false );
  }

  @Test
  public void testRemoveBackNavigationListenersTransportsListenOperationOnce() {
    RemoteObject remoteObject = TabrisTestUtil.mockServiceObject();
    App app = new AppImpl();
    BackNavigationListener listener = mock( BackNavigationListener.class );
    BackNavigationListener listener2 = mock( BackNavigationListener.class );
    app.addBackNavigationListener( listener );
    app.addBackNavigationListener( listener2 );

    app.removeBackNavigationListener( listener );
    app.removeBackNavigationListener( listener2 );

    InOrder order = inOrder( remoteObject );
    order.verify( remoteObject, times( 1 ) ).listen( "BackNavigation", true );
    order.verify( remoteObject, times( 1 ) ).listen( "BackNavigation", false );
  }

  @Test
  public void testRemoveOneBackNavigationListenerDoesNotTransportsListenOperation() {
    RemoteObject remoteObject = TabrisTestUtil.mockServiceObject();
    App app = new AppImpl();
    BackNavigationListener listener = mock( BackNavigationListener.class );
    BackNavigationListener listener2 = mock( BackNavigationListener.class );
    app.addBackNavigationListener( listener );
    app.addBackNavigationListener( listener2 );

    app.removeBackNavigationListener( listener );

    InOrder order = inOrder( remoteObject );
    order.verify( remoteObject, times( 1 ) ).listen( "BackNavigation", true );
    order.verify( remoteObject, never() ).listen( "BackNavigation", false );
  }

  @Test
  public void testDelegatesBackNavigationNotify() {
    AppImpl app = new AppImpl();
    BackNavigationListener listener = mock( BackNavigationListener.class );
    app.addBackNavigationListener( listener );

    TabrisTestUtil.dispatchNotify( app.getRemoteObject(), "BackNavigation", null );

    verify( listener ).navigatedBack();
  }

  @Test
  public void testDelegatesBackNavigationNotifyToAllListeners() {
    AppImpl app = new AppImpl();
    BackNavigationListener listener = mock( BackNavigationListener.class );
    BackNavigationListener listener2 = mock( BackNavigationListener.class );
    app.addBackNavigationListener( listener );
    app.addBackNavigationListener( listener2 );

    TabrisTestUtil.dispatchNotify( app.getRemoteObject(), "BackNavigation", null );

    verify( listener ).navigatedBack();
    verify( listener2 ).navigatedBack();
  }

  @Test( expected = IllegalArgumentException.class )
  public void testInactivityTimeoutFailsWithNegativeTime() {
    RemoteObject remoteObject = TabrisTestUtil.mockServiceObject();
    AppImpl app = new AppImpl();

    app.startInactivityTimer( -1 );

    ArgumentCaptor<JsonObject> captor = ArgumentCaptor.forClass( JsonObject.class );
    verify( remoteObject ).call( eq( "startInactivityTimer" ), captor.capture() );
    assertEquals( captor.getValue().get( "inactivityTime" ), JsonValue.valueOf( 10 ) );
  }

  @Test
  public void testCallsActivateInactivityLockOnRemoteObject() {
    RemoteObject remoteObject = TabrisTestUtil.mockServiceObject();
    AppImpl app = new AppImpl();

    app.startInactivityTimer( 10 );

    ArgumentCaptor<JsonObject> captor = ArgumentCaptor.forClass( JsonObject.class );
    verify( remoteObject ).call( eq( "startInactivityTimer" ), captor.capture() );
    assertEquals( captor.getValue().get( "inactivityTime" ), JsonValue.valueOf( 10 ) );
  }

  @Test
  public void testCallsDeactivateInactivityLockOnRemoteObject() {
    RemoteObject remoteObject = TabrisTestUtil.mockServiceObject();
    AppImpl app = new AppImpl();

    app.stopInactivityTimer();

    verify( remoteObject ).call( "stopInactivityTimer", null );
  }

  @Test
  public void testSetProtectScreenOnRemotObjectWithTrue() {
    RemoteObject remoteObject = TabrisTestUtil.mockServiceObject();
    AppImpl app = new AppImpl();

    app.setScreenProtected( true );

    verify( remoteObject ).set( "screenProtected", true );
  }

  @Test
  public void testSetProtectScreenOnRemotObjectOnlyOnChange() {
    RemoteObject remoteObject = TabrisTestUtil.mockServiceObject();
    AppImpl app = new AppImpl();

    app.setScreenProtected( true );
    app.setScreenProtected( true );

    verify( remoteObject, times( 1 ) ).set( "screenProtected", true );
  }

  @Test
  public void testSetProtectScreenOnRemotObjectWithFalse() {
    RemoteObject remoteObject = TabrisTestUtil.mockServiceObject();
    AppImpl app = new AppImpl();

    app.setScreenProtected( true );
    app.setScreenProtected( false );

    verify( remoteObject ).set( "screenProtected", false );
  }

  @Test
  public void testSavesScreenProtected() {
    AppImpl app = new AppImpl();

    app.setScreenProtected( true );

    assertTrue( app.isScreenProtected() );
  }

}
