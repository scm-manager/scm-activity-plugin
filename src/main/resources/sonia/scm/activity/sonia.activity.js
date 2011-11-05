/* *
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


Sonia.activity.ActivityGrid = Ext.extend(Sonia.repository.ChangesetViewerGrid, {

  repositoryTpl: '{0} - {1}',

  initComponent: function(){
    this.idsTemplate = this.idsTemplate.replace(/rel="{0}"/g, 'rel="{1}|{2}|{3}|{0}"');
    
    Sonia.activity.ActivityGrid.superclass.initComponent.apply(this, arguments);
    this.addColumn('repository-id', {
      id: 'repository-id',
      dataIndex: 'repository-id',
      width: 0,
      hidden: true,
      renderer: this.renderRepositoryColumn,
      scope: this
    });
    this.addColumn('date', {
      id: 'date',
      dataIndex: 'date',
      width: 0,
      hidden: true 
    });
  },
  
  renderIds: function(value, metaData, record){
    return String.format(
      this.idsTemplate, 
      value, 
      record.get('repository-id'),
      record.get('repository-name'),
      record.get('repository-type')
    );
  },
  
  renderRepositoryColumn: function(value, metaData, record){
    var type = repositoryTypeStore.queryBy(function(rec){
      return rec.get('name') == record.get('repository-type');
    }).itemAt(0);

    var typeName = null;
    if ( type ){
      typeName = type.get('displayName');
    } else {
      typeName = record.get('repository-type');
    }

    return String.format(
      this.repositoryTpl, 
      record.get('repository-name'),
      typeName
    );
  },
  
  createRepository: function(rev){
    return {
      id: rev[0],
      name: rev[1],
      type: rev[2]
    }
  },
  
  createRevision: function(rev){
    var revision = rev[3];
    var index = revision.indexOf(':');
    if ( index >= 0 ){
      revision = revision.substr(index+1);
    }
    return revision;
  },
  
  handleElement: function(el, callback){
    var rel = el.rel;
    var rev = rel.split('|');
    var repository = this.createRepository(rev);
    var revision = this.createRevision(rev);
    callback(repository, revision);
  },
  
  onClick: function(e){
    var el = e.getTarget('.cs-tree-link');
    
    if (el && el.rel){
      this.handleElement(el, this.openRepositoryBrowser);
    } else {      
      el = e.getTarget('.cs-diff-link');
      if ( el && el.rel ){
        this.handleElement(el, this.openDiffViewer);
      }
    }
  },
  
  openDiffViewer: function(repository, revision){
    main.addTab({
      id: 'diff-' + repository.id + ':' + revision,
      xtype: 'diffPanel',
      repository: repository,
      revision: revision,
      closable: true
    });
  },
  
  openRepositoryBrowser: function(repository, revision){
    main.addTab({
      id: 'repositorybrowser-' + repository.id + ':' + revision,
      xtype: 'repositoryBrowser',
      repository: repository,
      revision: revision,
      closable: true
    });
  }

});

// register xtype
Ext.reg('activityGrid', Sonia.activity.ActivityGrid);


Sonia.activity.ActivityViewerPanel = Ext.extend(Ext.Panel, {
  
  activityTitle: 'Activities',
  
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
      autoLoad: true,
      autoDestroy: true
    });
    
    var config = {
      title: this.activityTitle,
      items: [{
        id: 'activityGrid',
        xtype: 'activityGrid',
        store: this.activityStore,
        view: new Ext.grid.GroupingView({
          forceFit: false,
          // custom grouping text template to display the number of items per group
          groupTextTpl: '{group} / ({[values.rs.length]} {[values.rs.length > 1 ? "Changesets" : "Changeset"]})'
        })
      }]
    }

    Ext.apply(this, Ext.apply(this.initialConfig, config));
    Sonia.activity.ActivityViewerPanel.superclass.initComponent.apply(this, arguments);
  }
  
});

// register xtype
Ext.reg('activityViewerPanel', Sonia.activity.ActivityViewerPanel);

Ext.override(Sonia.scm.Main, {
  
  createHomePanel: function(){
    if ( debug ){
      console.debug('create home panel');
    }
    this.mainTabPanel.add({
      id: 'activities',
      xtype: 'activityViewerPanel',
      title: 'Activities',
      closeable: false,
      autoScroll: true
    });
    this.mainTabPanel.setActiveTab('activities');
  }
  
});