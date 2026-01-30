package com.astro5star.app.data.local.dao;

import androidx.annotation.NonNull;
import androidx.room.EntityInsertAdapter;
import androidx.room.RoomDatabase;
import androidx.room.coroutines.FlowUtil;
import androidx.room.util.DBUtil;
import androidx.room.util.SQLiteStatementUtil;
import androidx.sqlite.SQLiteStatement;
import com.astro5star.app.data.local.entity.ChatMessageEntity;
import java.lang.Class;
import java.lang.Object;
import java.lang.Override;
import java.lang.String;
import java.lang.SuppressWarnings;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import javax.annotation.processing.Generated;
import kotlin.Unit;
import kotlin.coroutines.Continuation;
import kotlinx.coroutines.flow.Flow;

@Generated("androidx.room.RoomProcessor")
@SuppressWarnings({"unchecked", "deprecation", "removal"})
public final class ChatDao_Impl implements ChatDao {
  private final RoomDatabase __db;

  private final EntityInsertAdapter<ChatMessageEntity> __insertAdapterOfChatMessageEntity;

  public ChatDao_Impl(@NonNull final RoomDatabase __db) {
    this.__db = __db;
    this.__insertAdapterOfChatMessageEntity = new EntityInsertAdapter<ChatMessageEntity>() {
      @Override
      @NonNull
      protected String createQuery() {
        return "INSERT OR REPLACE INTO `chat_messages` (`messageId`,`sessionId`,`text`,`senderId`,`timestamp`,`status`,`isSentByMe`) VALUES (?,?,?,?,?,?,?)";
      }

      @Override
      protected void bind(@NonNull final SQLiteStatement statement,
          @NonNull final ChatMessageEntity entity) {
        if (entity.getMessageId() == null) {
          statement.bindNull(1);
        } else {
          statement.bindText(1, entity.getMessageId());
        }
        if (entity.getSessionId() == null) {
          statement.bindNull(2);
        } else {
          statement.bindText(2, entity.getSessionId());
        }
        if (entity.getText() == null) {
          statement.bindNull(3);
        } else {
          statement.bindText(3, entity.getText());
        }
        if (entity.getSenderId() == null) {
          statement.bindNull(4);
        } else {
          statement.bindText(4, entity.getSenderId());
        }
        statement.bindLong(5, entity.getTimestamp());
        if (entity.getStatus() == null) {
          statement.bindNull(6);
        } else {
          statement.bindText(6, entity.getStatus());
        }
        final int _tmp = entity.isSentByMe() ? 1 : 0;
        statement.bindLong(7, _tmp);
      }
    };
  }

  @Override
  public Object insertMessage(final ChatMessageEntity message,
      final Continuation<? super Unit> $completion) {
    return DBUtil.performSuspending(__db, false, true, (_connection) -> {
      __insertAdapterOfChatMessageEntity.insert(_connection, message);
      return Unit.INSTANCE;
    }, $completion);
  }

  @Override
  public Object insertMessages(final List<ChatMessageEntity> messages,
      final Continuation<? super Unit> $completion) {
    return DBUtil.performSuspending(__db, false, true, (_connection) -> {
      __insertAdapterOfChatMessageEntity.insert(_connection, messages);
      return Unit.INSTANCE;
    }, $completion);
  }

  @Override
  public Flow<List<ChatMessageEntity>> getMessages(final String sessionId) {
    final String _sql = "SELECT * FROM chat_messages WHERE sessionId = ? ORDER BY timestamp ASC";
    return FlowUtil.createFlow(__db, false, new String[] {"chat_messages"}, (_connection) -> {
      final SQLiteStatement _stmt = _connection.prepare(_sql);
      try {
        int _argIndex = 1;
        if (sessionId == null) {
          _stmt.bindNull(_argIndex);
        } else {
          _stmt.bindText(_argIndex, sessionId);
        }
        final int _cursorIndexOfMessageId = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "messageId");
        final int _cursorIndexOfSessionId = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "sessionId");
        final int _cursorIndexOfText = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "text");
        final int _cursorIndexOfSenderId = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "senderId");
        final int _cursorIndexOfTimestamp = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "timestamp");
        final int _cursorIndexOfStatus = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "status");
        final int _cursorIndexOfIsSentByMe = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "isSentByMe");
        final List<ChatMessageEntity> _result = new ArrayList<ChatMessageEntity>();
        while (_stmt.step()) {
          final ChatMessageEntity _item;
          final String _tmpMessageId;
          if (_stmt.isNull(_cursorIndexOfMessageId)) {
            _tmpMessageId = null;
          } else {
            _tmpMessageId = _stmt.getText(_cursorIndexOfMessageId);
          }
          final String _tmpSessionId;
          if (_stmt.isNull(_cursorIndexOfSessionId)) {
            _tmpSessionId = null;
          } else {
            _tmpSessionId = _stmt.getText(_cursorIndexOfSessionId);
          }
          final String _tmpText;
          if (_stmt.isNull(_cursorIndexOfText)) {
            _tmpText = null;
          } else {
            _tmpText = _stmt.getText(_cursorIndexOfText);
          }
          final String _tmpSenderId;
          if (_stmt.isNull(_cursorIndexOfSenderId)) {
            _tmpSenderId = null;
          } else {
            _tmpSenderId = _stmt.getText(_cursorIndexOfSenderId);
          }
          final long _tmpTimestamp;
          _tmpTimestamp = _stmt.getLong(_cursorIndexOfTimestamp);
          final String _tmpStatus;
          if (_stmt.isNull(_cursorIndexOfStatus)) {
            _tmpStatus = null;
          } else {
            _tmpStatus = _stmt.getText(_cursorIndexOfStatus);
          }
          final boolean _tmpIsSentByMe;
          final int _tmp;
          _tmp = (int) (_stmt.getLong(_cursorIndexOfIsSentByMe));
          _tmpIsSentByMe = _tmp != 0;
          _item = new ChatMessageEntity(_tmpMessageId,_tmpSessionId,_tmpText,_tmpSenderId,_tmpTimestamp,_tmpStatus,_tmpIsSentByMe);
          _result.add(_item);
        }
        return _result;
      } finally {
        _stmt.close();
      }
    });
  }

  @Override
  public Object getMessagesList(final String sessionId,
      final Continuation<? super List<ChatMessageEntity>> $completion) {
    final String _sql = "SELECT * FROM chat_messages WHERE sessionId = ? ORDER BY timestamp ASC";
    return DBUtil.performSuspending(__db, true, false, (_connection) -> {
      final SQLiteStatement _stmt = _connection.prepare(_sql);
      try {
        int _argIndex = 1;
        if (sessionId == null) {
          _stmt.bindNull(_argIndex);
        } else {
          _stmt.bindText(_argIndex, sessionId);
        }
        final int _cursorIndexOfMessageId = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "messageId");
        final int _cursorIndexOfSessionId = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "sessionId");
        final int _cursorIndexOfText = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "text");
        final int _cursorIndexOfSenderId = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "senderId");
        final int _cursorIndexOfTimestamp = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "timestamp");
        final int _cursorIndexOfStatus = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "status");
        final int _cursorIndexOfIsSentByMe = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "isSentByMe");
        final List<ChatMessageEntity> _result = new ArrayList<ChatMessageEntity>();
        while (_stmt.step()) {
          final ChatMessageEntity _item;
          final String _tmpMessageId;
          if (_stmt.isNull(_cursorIndexOfMessageId)) {
            _tmpMessageId = null;
          } else {
            _tmpMessageId = _stmt.getText(_cursorIndexOfMessageId);
          }
          final String _tmpSessionId;
          if (_stmt.isNull(_cursorIndexOfSessionId)) {
            _tmpSessionId = null;
          } else {
            _tmpSessionId = _stmt.getText(_cursorIndexOfSessionId);
          }
          final String _tmpText;
          if (_stmt.isNull(_cursorIndexOfText)) {
            _tmpText = null;
          } else {
            _tmpText = _stmt.getText(_cursorIndexOfText);
          }
          final String _tmpSenderId;
          if (_stmt.isNull(_cursorIndexOfSenderId)) {
            _tmpSenderId = null;
          } else {
            _tmpSenderId = _stmt.getText(_cursorIndexOfSenderId);
          }
          final long _tmpTimestamp;
          _tmpTimestamp = _stmt.getLong(_cursorIndexOfTimestamp);
          final String _tmpStatus;
          if (_stmt.isNull(_cursorIndexOfStatus)) {
            _tmpStatus = null;
          } else {
            _tmpStatus = _stmt.getText(_cursorIndexOfStatus);
          }
          final boolean _tmpIsSentByMe;
          final int _tmp;
          _tmp = (int) (_stmt.getLong(_cursorIndexOfIsSentByMe));
          _tmpIsSentByMe = _tmp != 0;
          _item = new ChatMessageEntity(_tmpMessageId,_tmpSessionId,_tmpText,_tmpSenderId,_tmpTimestamp,_tmpStatus,_tmpIsSentByMe);
          _result.add(_item);
        }
        return _result;
      } finally {
        _stmt.close();
      }
    }, $completion);
  }

  @Override
  public Object updateStatus(final String messageId, final String status,
      final Continuation<? super Unit> $completion) {
    final String _sql = "UPDATE chat_messages SET status = ? WHERE messageId = ?";
    return DBUtil.performSuspending(__db, false, true, (_connection) -> {
      final SQLiteStatement _stmt = _connection.prepare(_sql);
      try {
        int _argIndex = 1;
        if (status == null) {
          _stmt.bindNull(_argIndex);
        } else {
          _stmt.bindText(_argIndex, status);
        }
        _argIndex = 2;
        if (messageId == null) {
          _stmt.bindNull(_argIndex);
        } else {
          _stmt.bindText(_argIndex, messageId);
        }
        _stmt.step();
        return Unit.INSTANCE;
      } finally {
        _stmt.close();
      }
    }, $completion);
  }

  @Override
  public Object clearSession(final String sessionId, final Continuation<? super Unit> $completion) {
    final String _sql = "DELETE FROM chat_messages WHERE sessionId = ?";
    return DBUtil.performSuspending(__db, false, true, (_connection) -> {
      final SQLiteStatement _stmt = _connection.prepare(_sql);
      try {
        int _argIndex = 1;
        if (sessionId == null) {
          _stmt.bindNull(_argIndex);
        } else {
          _stmt.bindText(_argIndex, sessionId);
        }
        _stmt.step();
        return Unit.INSTANCE;
      } finally {
        _stmt.close();
      }
    }, $completion);
  }

  @NonNull
  public static List<Class<?>> getRequiredConverters() {
    return Collections.emptyList();
  }
}
