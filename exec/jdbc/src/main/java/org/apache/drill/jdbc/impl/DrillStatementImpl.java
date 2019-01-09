/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.drill.jdbc.impl;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.SQLFeatureNotSupportedException;

import org.apache.calcite.avatica.AvaticaResultSet;
import org.apache.calcite.avatica.AvaticaStatement;
import org.apache.calcite.avatica.Meta.StatementHandle;
import org.apache.drill.common.exceptions.DrillRuntimeException;
import org.apache.drill.jdbc.AlreadyClosedSqlException;
import org.apache.drill.jdbc.DrillStatement;

/**
 * Drill's implementation of {@link java.sql.Statement}.
 */
// (Was abstract to avoid errors _here_ if newer versions of JDBC added
// interface methods, but now newer versions would probably use Java 8's default
// methods for compatibility.)
public class DrillStatementImpl extends AvaticaStatement implements DrillStatement,
                                                             DrillRemoteStatement {

  private final DrillConnectionImpl connection;

  DrillStatementImpl(DrillConnectionImpl connection, StatementHandle h, int resultSetType,
                     int resultSetConcurrency, int resultSetHoldability) {
    super(connection, h, resultSetType, resultSetConcurrency, resultSetHoldability);
    this.connection = connection;
    connection.openStatementsRegistry.addStatement(this);
  }

  /**
   * Throws AlreadyClosedSqlException <i>iff</i> this Statement is closed.
   *
   * @throws  AlreadyClosedSqlException  if Statement is closed
   */
  @Override
  protected void checkOpen() throws AlreadyClosedSqlException {
    if (isClosed()) {
      throw new AlreadyClosedSqlException( "Statement is already closed." );
    }
  }

  // Note:  Using dynamic proxies would reduce the quantity (450?) of method
  // overrides by eliminating those that exist solely to check whether the
  // object is closed.  It would also eliminate the need to throw non-compliant
  // RuntimeExceptions when Avatica's method declarations won't let us throw
  // proper SQLExceptions. (Check performance before applying to frequently
  // called ResultSet.)

  @Override
  public DrillConnectionImpl getConnection() throws SQLException {
    checkOpen();
    return connection;
  }

  @Override
  public long executeLargeUpdate(String sql, int autoGeneratedKeys) throws SQLException {
    checkOpen();
    return super.executeLargeUpdate(sql, autoGeneratedKeys);
  }

  @Override
  public long executeLargeUpdate(String sql, int[] columnIndexes) throws SQLException {
    checkOpen();
    return super.executeLargeUpdate(sql, columnIndexes);
  }

  @Override
  public long executeLargeUpdate(String sql, String[] columnNames) throws SQLException {
    checkOpen();
    return super.executeLargeUpdate(sql, columnNames);
  }

  @Override
  public int executeUpdate(String sql, int[] columnIndexes) throws SQLException {
    checkOpen();
    try {
      return super.executeUpdate(sql, columnIndexes);
    } catch (UnsupportedOperationException e) {
      throw new SQLFeatureNotSupportedException(e.getMessage(), e);
    }
  }

  @Override
  public int executeUpdate(String sql, String[] columnNames) throws SQLException {
    checkOpen();
    try {
      return super.executeUpdate(sql, columnNames);
    } catch (UnsupportedOperationException e) {
      throw new SQLFeatureNotSupportedException(e.getMessage(), e);
    }
  }

  @Override
  public void cleanUp() {
    connection.openStatementsRegistry.removeStatement(this);
  }

  @Override
  public boolean isClosed() {
    try {
      return super.isClosed();
    }
    catch ( SQLException e ) {
      // Currently can't happen, since AvaticaStatement.isClosed() never throws
      // SQLException.
      throw new DrillRuntimeException(
          "Unexpected exception from " + getClass().getSuperclass()
          + ".isClosed(): " + e,
          e );
    }
  }

  // Note:  Methods are in same order as in java.sql.Statement.

  // No isWrapperFor(Class<?>) (it doesn't throw SQLException if already closed).
  // No unwrap(Class<T>) (it doesn't throw SQLException if already closed).
  // No close() (it doesn't throw SQLException if already closed).

  @Override
  public void setEscapeProcessing(boolean enable) throws SQLException {
    checkOpen();
    try {
      super.setEscapeProcessing(enable);
    } catch (UnsupportedOperationException e) {
      throw new SQLFeatureNotSupportedException(e.getMessage(), e);
    }
  }

  @Override
  public void setCursorName(String name) throws SQLException {
    checkOpen();
    try {
      super.setCursorName(name);
    } catch (UnsupportedOperationException e) {
      throw new SQLFeatureNotSupportedException(e.getMessage(), e);
    }
  }

  @Override
  public boolean getMoreResults() throws SQLException {
    try {
      return super.getMoreResults();
    } catch (UnsupportedOperationException e) {
      throw new SQLFeatureNotSupportedException(e.getMessage(), e);
    }
  }

  @Override
  public void addBatch(String sql) throws SQLException {
    try {
      super.addBatch(sql);
    } catch (UnsupportedOperationException e) {
      throw new SQLFeatureNotSupportedException(e.getMessage(), e);
    }
  }

  @Override
  public void clearBatch() throws SQLException {
    try {
      super.clearBatch();
    } catch (UnsupportedOperationException e) {
      throw new SQLFeatureNotSupportedException(e.getMessage(), e);
    }
  }

  @Override
  public int[] executeBatch() throws SQLException {
    checkOpen();
    try {
      return super.executeBatch();
    } catch (UnsupportedOperationException e) {
      throw new SQLFeatureNotSupportedException(e.getMessage(), e);
    }
  }

  @Override
  public boolean getMoreResults(int current) throws SQLException {
    try {
      return super.getMoreResults(current);
    } catch (UnsupportedOperationException e) {
      throw new SQLFeatureNotSupportedException(e.getMessage(), e);
    }
  }

  @Override
  public ResultSet getGeneratedKeys() throws SQLException {
    checkOpen();
    try {
      return super.getGeneratedKeys();
    } catch (UnsupportedOperationException e) {
      throw new SQLFeatureNotSupportedException(e.getMessage(), e);
    }
  }

  @Override
  public int executeUpdate(String sql, int autoGeneratedKeys) throws SQLException {
    checkOpen();
    try {
      return super.executeUpdate(sql, autoGeneratedKeys);
    } catch (UnsupportedOperationException e) {
      throw new SQLFeatureNotSupportedException(e.getMessage(), e);
    }
  }

  @Override
  public boolean execute(String sql, int autoGeneratedKeys) throws SQLException {
    checkOpen();
    try {
      return super.execute(sql, autoGeneratedKeys);
    } catch (UnsupportedOperationException e) {
      throw new SQLFeatureNotSupportedException(e.getMessage(), e);
    }
  }

  @Override
  public boolean execute(String sql, int columnIndexes[]) throws SQLException {
    checkOpen();
    try {
      return super.execute(sql, columnIndexes);
    } catch (UnsupportedOperationException e) {
      throw new SQLFeatureNotSupportedException(e.getMessage(), e);
    }
  }

  @Override
  public boolean execute(String sql, String columnNames[]) throws SQLException {
    checkOpen();
    try {
      return super.execute(sql, columnNames);
    } catch (UnsupportedOperationException e) {
      throw new SQLFeatureNotSupportedException(e.getMessage(), e);
    }
  }

  @Override
  public void setPoolable(boolean poolable) throws SQLException {
    checkOpen();
    try {
      super.setPoolable(poolable);
    } catch (UnsupportedOperationException e) {
      throw new SQLFeatureNotSupportedException(e.getMessage(), e);
    }
  }

  @Override
  public void setResultSet(AvaticaResultSet resultSet) {
    openResultSet = resultSet;
  }

  @Override
  public void setUpdateCount(int value) {
    updateCount = value;
  }
}
