import Ember from 'ember';

export default Ember.Route.extend({
  afterModel(p, transition) {
    if (transition.targetName === this.routeName + '.index') {
      this.transitionTo('main.current')
    }
  }
});
