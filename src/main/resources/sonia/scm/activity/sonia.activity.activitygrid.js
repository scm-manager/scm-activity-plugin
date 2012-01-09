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
  newVersion: false,

  initComponent: function(){
    this.newVersion = this.isNewVersion();
    if ( ! this.newVersion ){
      if ( debug ){
        console.debug('detected scm-manager version pre 1.11');
      }
      this.idsTemplate = this.idsTemplate.replace(/rel="{0}"/g, 'rel="{1}|{2}|{3}|{0}"');
    } else if ( debug ) {
      console.debug('detected scm-manager version post 1.11');
    }
    
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
  
  isNewVersion: function(){
    var result = false;
    try {
      var version = state.version;
      var parts = version.split('\.');
      if ( parts[0] != '1' ){
        result = true;
      } else {
        parts = parts[1].split('-');
        result = parseInt(parts[0]) > 10;
      }
    } catch (e){
      if (debug){
        console.debug(e);
      }
    }
    return result;
  },
  
  renderIds: function(value, metaData, record){
    var result = null;
    if (this.newVersion){
      result = this.renderIdsNew(value, record);
    } else {
      result = this.renderIdsOld(value, record);
    }
    return result;
  },
  
  renderIdsOld: function(value, record){
    return String.format(
      this.idsTemplate, 
      value, 
      record.get('repository-id'),
      record.get('repository-name'),
      record.get('repository-type')
    );
  },
  
  renderIdsNew: function(value, record){
    var parent = null;
    var parent2 = null;
    var parents = record.get('parents');
    if ( parents ){
      parent = parents[0];
      if ( parents.length >= 1 ){
        parent2 = parents[1];
      }
    }
    var ids = this.idsTemplate.apply({
      id: value,
      parent: parent,
      parent2: parent2
    });
    
    ids = this.replaceIdRel(ids, value, record);
    ids = this.replaceIdRel(ids, parent, record);
    ids = this.replaceIdRel(ids, parent2, record);
    
    return ids;
  },
  
  replaceIdRel: function(ids, value, record){
    if (value){
      var regex = new RegExp('rel="' + value + '"' , 'g');
      //ids = regex.replace(ids, 'rel="' + this.createIdRel(value, record) + '"');
      ids = ids.replace(regex, 'rel="' + this.createIdRel(value, record) + '"');
    }
    return ids;
  },
  
  createIdRel: function(value, record){
    if (value){
      value = record.get('repository-id') + '|' + 
              record.get('repository-name') + '|' + 
              record.get('repository-type') + '|' + 
              value;
    }
    return value;
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
    var rev = el.rel.split('|');
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
