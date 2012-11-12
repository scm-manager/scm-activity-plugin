/*
 * Copyright (c) 2010, Sebastian Sdorra
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * 1. Redistributions of source code must retain the above copyright notice,
 *    this list of conditions and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the documentation
 *    and/or other materials provided with the distribution.
 * 3. Neither the name of SCM-Manager; nor the names of its
 *    contributors may be used to endorse or promote products derived from this
 *    software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED.  IN NO EVENT SHALL THE REGENTS OR CONTRIBUTORS BE LIABLE FOR ANY
 * DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON
 * ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 *
 * http://bitbucket.org/sdorra/scm-manager
 *
 */
Ext.ns("Sonia.activity");


Sonia.activity.ActivityViewerPanel = Ext.extend(Ext.Panel, {
  
  activityTitle: 'Activities',
  emptyText: 'No activities available',
  
  // 5min
  refreshInterval: 300000,
  
  initComponent: function(){
    
    this.activityStore = new Ext.data.GroupingStore({
      id: 'activityStore',
      proxy: new Ext.data.HttpProxy({
        url: restUrl + 'activity.json',
        method: 'GET'
      }),
      reader: new Ext.data.JsonReader({
        fields: [{ 
          name: 'id',
          mapping: 'changeset.id'
        },{
          name: 'date',
          mapping: 'changeset.date'
        }, {
          name: 'author',
          mapping: 'changeset.author'
        },{
          name: 'description',
          mapping: 'changeset.description'
        },{
          name: 'modifications',
          mapping: 'changeset.modifications'
        },{
          name: 'tags',
          mapping: 'changeset.tags'
        },{
          name: 'branches',
          mapping: 'changeset.branches'
        },{
          name: 'parents',
          mapping: 'changeset.parents'
        },{
          name: 'properties',
          mapping: 'changeset.properties'
        },{
          name: 'repository-id'
        },{
          name: 'repository-name'
        },{
          name: 'repository-type'
        }],
        root: 'activities'
      }),
      remoteSort: true,
      remoteGroup: true,
      groupOnSort: false,
      groupField: 'repository-id',
      groupDir: 'AES',
      autoLoad: false,
      autoDestroy: true
    });
    
    var config = {
      title: this.activityTitle,
      items: [{
        id: 'activityGrid',
        xtype: 'activityGrid',
        store: this.activityStore,
        view: new Ext.grid.GroupingView({
          emptyText: this.emptyText,
          forceFit: false,
          // custom grouping text template to display the number of items per group
          groupTextTpl: '{group} / ({[values.rs.length]} {[values.rs.length > 1 ? "Changesets" : "Changeset"]})'
        })
      }]
    }
    
    Ext.TaskMgr.start({
      run: this.refreshStore,
      interval: this.refreshInterval,
      scope: this
    });

    Ext.apply(this, Ext.apply(this.initialConfig, config));
    Sonia.activity.ActivityViewerPanel.superclass.initComponent.apply(this, arguments);
  },
  
  refreshStore: function(){
    this.activityStore.reload();
  }
  
});

// register xtype
Ext.reg('activityViewerPanel', Sonia.activity.ActivityViewerPanel);
