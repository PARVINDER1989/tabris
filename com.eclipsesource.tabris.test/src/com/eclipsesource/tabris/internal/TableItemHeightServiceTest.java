/*******************************************************************************
 * Copyright (c) 2014 EclipseSource and others. All rights reserved. This
 * program and the accompanying materials are made available under the terms of
 * the Eclipse Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/epl-v10.html Contributors:
 * EclipseSource - initial API and implementation
 ******************************************************************************/
package com.eclipsesource.tabris.internal;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

import java.io.Serializable;

import org.eclipse.rap.json.JsonObject;
import org.eclipse.rap.rwt.internal.remote.RemoteObjectRegistry;
import org.eclipse.rap.rwt.lifecycle.PhaseId;
import org.eclipse.rap.rwt.lifecycle.WidgetUtil;
import org.eclipse.rap.rwt.testfixture.Fixture;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.Widget;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

@SuppressWarnings("restriction")
public class TableItemHeightServiceTest {

  private Table table;
  private Tree tree;
  private JsonObject parameters;
  private TableItemHeightService itemHeightService;

  @Before
  public void setUp() {
    Fixture.setUp();
    Fixture.fakePhase( PhaseId.PROCESS_ACTION );
    Display display = new Display();
    Shell shell = new Shell( display );
    table = new Table( shell, SWT.NONE );
    tree = new Tree(shell, SWT.NONE);
    parameters = new JsonObject();
    itemHeightService = new TableItemHeightService();
  }

  @After
  public void tearDown() {
    Fixture.tearDown();
  }

  @Test
  public void testIsSerializable() {
    assertTrue( Serializable.class.isAssignableFrom( TableItemHeightService.class ) );
  }

  @Test
  public void testServiceCreated() {
    assertNotNull( RemoteObjectRegistry.getInstance().get( Constants.GRID_ITEM_HEIGHT_SETTER ) );
  }

  @Test
  public void testSetTableItemHeight() {
    parameters.set( "target",  WidgetUtil.getId( table ) );
    parameters.set( "itemHeight", 45 );

    itemHeightService.handleCall( "setItemHeight", parameters );

    assertEquals( Integer.valueOf( 45 ), table.getData( "org.eclipse.rap.rwt.customItemHeight" ) );
  }

  @Test
  public void testSetTreeItemHeight() {
    parameters.set( "target",  WidgetUtil.getId( tree ) );
    parameters.set( "itemHeight", 47 );

    itemHeightService.handleCall( "setItemHeight", parameters );

    assertEquals( Integer.valueOf( 47 ), tree.getData( "org.eclipse.rap.rwt.customItemHeight" ) );
  }

  @Test
  public void setItemHeightDisposedWidget() {
    parameters.set( "target",  WidgetUtil.getId( tree ) );
    parameters.set( "itemHeight", 47 );
    tree.dispose();

    itemHeightService.handleCall( "setItemHeight", parameters );

    // Just making sure we don't throw a Widget Disposed Exception
  }

  @Test
  public void testFindWidget() {
    Widget result = itemHeightService.findWidget( WidgetUtil.getId( table ) );

    assertSame( table, result );
  }

  @Test
  public void testCannotFindWidget() {
    Widget result = itemHeightService.findWidget( "bar" );

    assertNull( result );
  }
}
