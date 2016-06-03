package com.beraaksoy.whatplace;

import android.content.Intent;
import android.widget.RemoteViewsService;

public class PlaceWidgetService extends RemoteViewsService {
  @Override
  public RemoteViewsFactory onGetViewFactory(Intent intent) {
    return(new PlaceViewsFactory(this.getApplicationContext(),
                                 intent));
  }
}