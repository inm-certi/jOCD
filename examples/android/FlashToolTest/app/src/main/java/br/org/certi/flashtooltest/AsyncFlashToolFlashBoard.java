/*
 * Copyright 2018 Fundação CERTI
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
 * See the license for the specific language governing permissions and
 * limitations under the license.
 */
package br.org.certi.flashtooltest;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;
import br.org.certi.jocd.Jocd.ErrorCode;
import br.org.certi.jocd.board.MbedBoard;
import br.org.certi.jocd.dapaccess.dapexceptions.InsufficientPermissions;
import br.org.certi.jocd.tools.FlashTool;
import br.org.certi.jocd.tools.ProgressUpdateInterface;

public class AsyncFlashToolFlashBoard extends AsyncTask<String, Integer, String> implements
    ProgressUpdateInterface {

  // Logging
  private static final String TAG = "AsyncFlashBoard";

  // Callback to the UI activity.
  private AsyncResponse.FlashBoard delegate = null;

  // Path to file.
  private String flashFilePath;

  // Context (for USB Manager).
  private Context context = null;
  private boolean exceptionOccurred = false;

  double startTime = 0;

  /*
   * Constructor.
   */
  public AsyncFlashToolFlashBoard(Context context, AsyncResponse.FlashBoard delegate,
      String flashFilePath) {
    this.context = context;
    this.delegate = delegate;
    this.flashFilePath = flashFilePath;
  }

  @Override
  protected String doInBackground(String... params) {
    ErrorCode errorCode;

    if (this.flashFilePath == null) {
      this.exceptionOccurred = true;
      onException(new Exception("Can't flash without a file."));
      return null;
    }

    FlashTool tool = new FlashTool();
    this.exceptionOccurred = false;

    startTime = System.currentTimeMillis();

    try {
      errorCode = tool.flashBoard(this.flashFilePath, this);
    } catch (Exception exception) {
      this.exceptionOccurred = true;
      onException(exception);
      return null;
    }

    return errorCode.toString();
  }

  @Override
  protected void onProgressUpdate(Integer... percentage) {
    if (this.exceptionOccurred) {
      return;
    }

    Log.d(TAG, "Flashing device... " + percentage[0] + "%");
    delegate.processAsyncTaskUpdate("Flashing device... " + percentage[0] + "%");
  }

  @Override
  protected void onPostExecute(String result) {
    if (this.exceptionOccurred) {
      return;
    }

    double elapsed = System.currentTimeMillis();
    elapsed -=  startTime;
    Toast.makeText(this.context, String.format("elapsed: %.02f", elapsed),
        Toast.LENGTH_LONG).show();

    Log.d(TAG, result);
    delegate.processAsyncTaskFinish(result);
  }

  protected void onException(Exception exception) {
    Log.d(TAG, "Exception " + exception.getMessage());

    if (exception instanceof MbedBoard.NoBoardConnectedException) {
      Log.e(TAG, "No board connected");
    } else if (exception instanceof MbedBoard.UniqueIDNotFoundException) {
      Log.e(TAG, "Unique ID not found");
    } else if (exception instanceof MbedBoard.UnspecifiedBoardIDException) {
      Log.e(TAG, "Unspecified board ID");
    } else if (exception instanceof InsufficientPermissions) {
      Log.e(TAG, "Insufficient permissions to access USB device");
    } else {
      Log.e(TAG, "Exception caught: " + exception.getMessage());
    }

    delegate.processAsyncException(exception);
  }

  /*
   * Callback to receive the current percentage and
   * publish the progress.
   */
  public void progressUpdateCallback(int percentage) {
    publishProgress(percentage);
  }
}
