import { test } from 'qunit';
import moduleForAcceptance from 'gtd/tests/helpers/module-for-acceptance';

moduleForAcceptance('Acceptance | main');

test('visiting /main and redirected to current sub route', function(assert) {
  visit('/main');

  andThen(function() {
    assert.equal(currentURL(), '/main/current');
  });
});

test('"Current" and "Archive" routes should be accessible', function (assert) {
  visit('/main');

  click('a.t_link-to-archive').then(() => {
    assert.equal(currentURL(), '/main/archive');
  });

  click('a.t_link-to-current').then(() => {
    assert.equal(currentURL(), '/main/current');
  });

});
